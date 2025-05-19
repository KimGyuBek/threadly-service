package com.threadly.post.comment;

import com.threadly.post.comment.command.DeletePostCommentCommand;

/**
 * 게시글 댓글 변경 관련 UseCase
 */
public interface UpdatePostCommentUseCase {

  /**
   * DELETED 상태로 변경
   *
   * @param command
   */
  void deletePostComment(DeletePostCommentCommand command);

}
