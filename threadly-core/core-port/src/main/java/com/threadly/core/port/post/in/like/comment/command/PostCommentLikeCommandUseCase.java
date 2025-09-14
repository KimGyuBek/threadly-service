package com.threadly.core.port.post.in.like.comment.command;

import com.threadly.core.port.post.in.like.comment.command.dto.LikePostCommentApiResponse;
import com.threadly.core.port.post.in.like.comment.command.dto.LikePostCommentCommand;

/**
 * 게시글 댓글 좋아요 취소 관련 UseCase
 */
public interface PostCommentLikeCommandUseCase {

  /**
   * 좋아요 취소
   * @param command
   * @return
   */
  LikePostCommentApiResponse cancelPostCommentLike(LikePostCommentCommand command);

  /**
   * 댓글 좋아요
   * @param command
   * @return
   */
  LikePostCommentApiResponse likePostComment(LikePostCommentCommand command);

}
