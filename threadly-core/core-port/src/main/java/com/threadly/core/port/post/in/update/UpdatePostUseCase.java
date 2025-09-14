package com.threadly.core.port.post.in.update;

/**
 * 게시글 수정 관련 UseCase
 */
public interface UpdatePostUseCase {

  /**
   * 게시글 수정
   *
   * @param command
   * @return
   */
  UpdatePostApiResponse updatePost(UpdatePostCommand command);

}
