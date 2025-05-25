package com.threadly.post.like.comment;

/**
 * 댓글 좋아요 관련 UseCase
 */
public interface LikePostCommentUseCase {

  /**
   * 댓글 좋아요
   * @param command
   * @return
   */
  LikePostCommentApiResponse likePostComment(LikePostCommentCommand command);

}
