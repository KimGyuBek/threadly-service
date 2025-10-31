package com.threadly.core.service.post.like.comment;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.post.PostCommentException;
import com.threadly.core.domain.notification.NotificationType;
import com.threadly.core.domain.notification.metadata.CommentLikeMeta;
import com.threadly.core.domain.post.comment.CannotLikePostCommentException;
import com.threadly.core.domain.post.comment.PostComment;
import com.threadly.core.port.post.in.like.comment.command.PostCommentLikeCommandUseCase;
import com.threadly.core.port.post.in.like.comment.command.dto.LikePostCommentApiResponse;
import com.threadly.core.port.post.in.like.comment.command.dto.LikePostCommentCommand;
import com.threadly.core.port.post.out.comment.PostCommentQueryPort;
import com.threadly.core.port.post.out.like.comment.PostCommentLikerCommandPort;
import com.threadly.core.port.post.out.like.comment.PostCommentLikeQueryPort;
import com.threadly.core.service.notification.dto.NotificationPublishCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시글 댓글 좋아요 관련 Service
 */
@Service
@RequiredArgsConstructor
public class PostCommentLikeCommandService implements PostCommentLikeCommandUseCase {

  private final PostCommentQueryPort postCommentQueryPort;

  private final PostCommentLikeQueryPort postCommentLikeQueryPort;
  private final PostCommentLikerCommandPort postCommentLikerCommandPort;

  private final ApplicationEventPublisher applicationEventPublisher;

  @Transactional
  @Override
  public LikePostCommentApiResponse likePostComment(LikePostCommentCommand command) {

    /*게시글 댓글 조회*/
    PostComment postComment = getPostComment(command);

    /*좋아요 가능한지 검증*/
    validateLikeable(postComment);

    /*좋아요 누르지 않았다면*/
    if (!isUserLiked(command)) {
      /*좋아요 처리*/
      postCommentLikerCommandPort.createPostCommentLike(
          postComment.like(command.getUserId()));
    }

    /*알림 이벤트 발행*/
    applicationEventPublisher.publishEvent(
        new NotificationPublishCommand(
            postComment.getUserId(),
            command.getUserId(),
            NotificationType.COMMENT_LIKE,
            new CommentLikeMeta(
                postComment.getPostId(),
                postComment.getCommentId(),
                postComment.getContent()
            )
        )
    );

    return new LikePostCommentApiResponse(
        postComment.getCommentId(),
        getLikeCount(command)
    );
  }


  @Transactional
  @Override
  public LikePostCommentApiResponse cancelPostCommentLike(LikePostCommentCommand command) {
    /*게시글 댓글 조회*/
    PostComment postComment = getPostComment(command);

    /*좋아요 취소 가능한 상태인지 검증*/
    validateLikeable(postComment);

    /*좋아요를 누른 상태인 경우*/
    if (isUserLiked(command)) {
      /*좋아요 삭제*/
      postCommentLikerCommandPort.deletePostCommentLike(command.getCommentId(), command.getUserId());
    }

    return new LikePostCommentApiResponse(
        postComment.getCommentId(),
        getLikeCount(command)
    );
  }

  /**
   * 좋아요 수 조회
   *
   * @param command
   * @return
   */
  private long getLikeCount(LikePostCommentCommand command) {
    long likeCount = postCommentLikeQueryPort.fetchLikeCountByCommentId(
        command.getCommentId());
    return likeCount;
  }

  /**
   * 게시글 댓글 조회
   *
   * @param command
   * @return
   */
  private PostComment getPostComment(LikePostCommentCommand command) {
    return
        postCommentQueryPort.fetchById(command.getCommentId())
            .orElseThrow(() -> new PostCommentException(
                ErrorCode.POST_COMMENT_NOT_FOUND));
  }

  /**
   * 좋아요 가능한지 검증
   *
   * @param postComment
   */
  private static void validateLikeable(PostComment postComment) {
    try {
      postComment.validateLikeable();
    } catch (CannotLikePostCommentException e) {
      throw new PostCommentException(ErrorCode.POST_COMMENT_LIKE_NOT_ALLOWED);
    }
  }

  /**
   * 사용자가 이미 좋아요 눌렀는지 검증
   *
   * @param command
   * @return
   */
  private boolean isUserLiked(LikePostCommentCommand command) {
    return postCommentLikeQueryPort.existsByCommentIdAndUserId(command.getCommentId(),
        command.getUserId());
  }
}
