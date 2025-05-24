package com.threadly.post;

import com.threadly.post.command.CreatePostCommand;
import com.threadly.post.response.CreatePostApiResponse;

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
