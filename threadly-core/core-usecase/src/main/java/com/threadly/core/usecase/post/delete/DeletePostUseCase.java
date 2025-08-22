package com.threadly.core.usecase.post.delete;

/**
 * 게시글 삭제 관련 UseCase
 */
public interface DeletePostUseCase {

  /**
   * 게시글 삭제 상태로 변경
   * @param command
   */
  void softDeletePost(DeletePostCommand command);

}
