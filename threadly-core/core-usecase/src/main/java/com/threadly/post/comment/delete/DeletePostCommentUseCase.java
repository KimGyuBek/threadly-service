package com.threadly.post.comment.delete;

/**
 * 게시글 댓글 변경 관련 UseCase
 */
public interface DeletePostCommentUseCase {

  /**
   * DELETED 상태로 변경
   *
   * @param command
   */
  void softDeletePostComment(DeletePostCommentCommand command);

}
