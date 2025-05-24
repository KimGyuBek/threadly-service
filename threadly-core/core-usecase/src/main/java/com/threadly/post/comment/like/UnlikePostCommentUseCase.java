package com.threadly.post.comment.like;

import com.threadly.post.comment.like.command.LikePostCommentCommand;
import com.threadly.post.comment.like.response.LikePostCommentApiResponse;

/**
 * 게시글 댓글 좋아요 취소 관련 UseCase
 */
public interface UnlikePostCommentUseCase {

  /**
   * 좋아요 취소
   * @param command
   * @return
   */
  LikePostCommentApiResponse cancelPostCommentLike(LikePostCommentCommand command);

}
