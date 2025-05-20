package com.threadly.post.comment.like;

import com.threadly.post.comment.like.command.LikePostCommentCommand;
import com.threadly.post.comment.like.response.LikePostCommentApiResponse;

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
