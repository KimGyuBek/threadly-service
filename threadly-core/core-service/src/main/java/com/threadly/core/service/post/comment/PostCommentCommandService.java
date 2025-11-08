package com.threadly.core.service.post.comment;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.post.PostCommentException;
import com.threadly.core.domain.notification.NotificationType;
import com.threadly.core.domain.notification.metadata.PostCommentMeta;
import com.threadly.core.domain.post.Post;
import com.threadly.core.domain.post.PostCommentStatus;
import com.threadly.core.domain.post.PostStatus;
import com.threadly.core.domain.post.comment.CannotDeleteCommentException.AlreadyDeletedException;
import com.threadly.core.domain.post.comment.CannotDeleteCommentException.BlockedException;
import com.threadly.core.domain.post.comment.CannotDeleteCommentException.ParentPostInactiveException;
import com.threadly.core.domain.post.comment.CannotDeleteCommentException.WriteMismatchException;
import com.threadly.core.domain.post.comment.PostComment;
import com.threadly.core.port.post.in.comment.command.PostCommentCommandUseCase;
import com.threadly.core.port.post.in.comment.command.dto.CreatePostCommentApiResponse;
import com.threadly.core.port.post.in.comment.command.dto.CreatePostCommentCommand;
import com.threadly.core.port.post.in.comment.command.dto.DeletePostCommentCommand;
import com.threadly.core.port.post.out.comment.PostCommentCommandPort;
import com.threadly.core.port.post.out.like.comment.PostCommentLikerCommandPort;
import com.threadly.core.port.user.out.profile.UserProfileQueryPort;
import com.threadly.core.port.user.out.profile.projection.UserPreviewProjection;
import com.threadly.core.service.notification.dto.NotificationPublishCommand;
import com.threadly.core.service.validator.post.PostCommentValidator;
import com.threadly.core.service.validator.post.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시글 댓글 관련 Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PostCommentCommandService implements PostCommentCommandUseCase {

  private final PostValidator postValidator;
  private final PostCommentValidator postCommentValidator;

  private final PostCommentCommandPort postCommentCommandPort;
  private final PostCommentLikerCommandPort postCommentLikerCommandPort;
  private final UserProfileQueryPort userProfileQueryPort;

  private final ApplicationEventPublisher applicationEventPublisher;

  @Transactional
  @Override
  public CreatePostCommentApiResponse createPostComment(CreatePostCommentCommand command) {
    /* 게시글 조회*/
    Post post = postValidator.getPostOrThrow(command.getPostId());

    /*게시글 상태 검증*/
    postValidator.validateAccessibleStatus(post.getStatus());

    /*게시글 생성*/
    PostComment newComment = post.addComment(command.getCommenterId(), command.getContent());

    /*댓글 저장*/
    postCommentCommandPort.savePostComment(newComment);

    /*comment preview 조회*/
    UserPreviewProjection userCommentPreview = userProfileQueryPort.findUserPreviewByUserId(
        newComment.getUserId());

    /*Notification 발행*/
    applicationEventPublisher.publishEvent(
        new NotificationPublishCommand(
            post.getUserId(),
            newComment.getUserId(),
            NotificationType.COMMENT_ADDED,
            new PostCommentMeta(
                post.getPostId(),
                newComment.getCommentId(),
                newComment.getContent()
            )
        )
    );

    return new CreatePostCommentApiResponse(
        newComment.getCommentId(),
        newComment.getUserId(),
        userCommentPreview.getNickname(),
        userCommentPreview.getProfileImageUrl(),
        newComment.getContent(),
        newComment.getCreatedAt()
    );
  }

  @Transactional
  @Override
  public void softDeletePostComment(DeletePostCommentCommand command) {
    /*댓글 조회*/
    PostComment postComment = postCommentValidator.getPostCommentOrThrow(
        command.getCommentId());

    /*게시글 상태 조회*/
    PostStatus postStatus = postValidator.validateAccessibleStatusById(command.getPostId());

    /*게시글 댓글 삭제 가능한지 검증*/
    validateDeletable(command, postComment, postStatus);

    /*댓글 삭제 상태로 변경*/
    postComment.markAsDeleted();
    postCommentCommandPort.updatePostCommentStatus(postComment.getCommentId(),
        postComment.getStatus());

    log.info("게시글 댓글 삭제 완료, commentId: {}", command.getCommentId());
  }

  /**
   * 게시글 댓글이 삭제 가능한 상태인지 검증
   *
   * @param command
   * @param postComment
   * @param postStatus
   */
  private static void validateDeletable(DeletePostCommentCommand command, PostComment postComment,
      PostStatus postStatus) {
    try {
      postComment.validateDeletableBy(command.getUserId(), postStatus);
    } catch (WriteMismatchException e) {
      throw new PostCommentException(ErrorCode.POST_COMMENT_DELETE_FORBIDDEN);
    } catch (AlreadyDeletedException e) {
      throw new PostCommentException(ErrorCode.POST_COMMENT_ALREADY_DELETED);
    } catch (BlockedException e) {
      throw new PostCommentException(ErrorCode.POST_COMMENT_DELETE_BLOCKED);
    } catch (ParentPostInactiveException e) {
      throw new PostCommentException(ErrorCode.POST_COMMENT_PARENT_POST_INACTIVE);
    }
  }

  @Transactional
  @Override
  public void deleteAllCommentsAndLikesByPostId(String postId) {
    /*댓글 삭제 상태로 변경*/
    postCommentCommandPort.updateAllCommentStatusByPostId(postId, PostCommentStatus.DELETED);

    /*좋아요 목록 전체 삭제*/
    postCommentLikerCommandPort.deleteAllByPostId(postId);
  }
}
