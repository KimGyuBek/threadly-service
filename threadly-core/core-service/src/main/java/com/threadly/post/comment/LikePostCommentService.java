package com.threadly.post.comment;

import com.threadly.ErrorCode;
import com.threadly.exception.post.PostCommentException;
import com.threadly.post.comment.like.CancelPostCommentLikeUseCase;
import com.threadly.post.comment.like.CreatePostCommentLikePort;
import com.threadly.post.comment.like.DeletePostCommentLikePort;
import com.threadly.post.comment.like.FetchPostCommentLikePort;
import com.threadly.post.comment.like.LikePostCommentUseCase;
import com.threadly.post.comment.like.command.LikePostCommentCommand;
import com.threadly.post.comment.like.response.LikePostCommentApiResponse;
import com.threadly.posts.comment.CannotLikePostCommentException;
import com.threadly.posts.comment.PostComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시글 좋아요 관련 Service
 */
@Service
@RequiredArgsConstructor
public class LikePostCommentService implements LikePostCommentUseCase,
    CancelPostCommentLikeUseCase {

  private final FetchPostCommentPort fetchPostCommentPort;

  private final FetchPostCommentLikePort fetchPostCommentLikePort;
  private final CreatePostCommentLikePort createPostCommentLikePort;
  private final DeletePostCommentLikePort deletePostCommentLikePort;

  @Transactional
  @Override
  public LikePostCommentApiResponse likePostComment(LikePostCommentCommand command) {

    /*게시글 댓글 조회*/
    PostComment postComment = fetchPostComment(command);

    /*좋아요 가능한지 검증*/
    validateLikeable(postComment);

    /*좋아요 누르지 않았다면*/
    if (!isUserLiked(command)) {

      /*좋아요 처리*/
      createPostCommentLikePort.createPostCommentLike(
          postComment.like(command.getUserId()));
    }

    /*좋아요 수 조회*/
    long likeCount = fetchLikeCount(command);

    return new LikePostCommentApiResponse(
        postComment.getCommentId(),
        likeCount
    );
  }


  @Transactional
  @Override
  public LikePostCommentApiResponse cancelPostCommentLike(LikePostCommentCommand command) {
    /*게시글 댓글 조회*/
    PostComment postComment = fetchPostComment(command);

    /*좋아요 취소 가능한 상태인지 검증*/
    validateLikeable(postComment);

    /*사용자가 좋아요를 누른 상태인지 검증*/
    /*좋아요를 누른 상태인 경우*/
    if (isUserLiked(command)) {
      /*좋아요 삭제*/
      deletePostCommentLikePort.deletePostCommentLike(command.getCommentId(), command.getUserId());
    }

    long likeCount = fetchLikeCount(command);

    return new LikePostCommentApiResponse(
        postComment.getCommentId(),
        likeCount
    );
  }

  /**
   * 좋아요 수 조회
   *
   * @param command
   * @return
   */
  private long fetchLikeCount(LikePostCommentCommand command) {
    long likeCount = fetchPostCommentLikePort.getLikeCountByCommentId(
        command.getCommentId());
    return likeCount;
  }

  /**
   * 게시글 댓글 조회
   *
   * @param command
   * @return
   */
  private PostComment fetchPostComment(LikePostCommentCommand command) {
    return
        fetchPostCommentPort.findById(command.getCommentId())
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
    return fetchPostCommentLikePort.existsByCommentIdAndUserId(command.getCommentId(),
        command.getUserId());
  }
}
