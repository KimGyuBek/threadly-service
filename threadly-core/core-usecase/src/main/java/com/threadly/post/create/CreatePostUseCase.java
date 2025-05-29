package com.threadly.post.create;

/**
 * 게시글 생성 관련 UseCase
 */
public interface CreatePostUseCase {

  /**
   * 게시글 생성
   * @param command
   * @return
   */
  CreatePostApiResponse createPost(CreatePostCommand command);


}
