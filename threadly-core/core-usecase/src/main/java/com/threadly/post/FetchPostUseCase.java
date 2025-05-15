package com.threadly.post;

import com.threadly.post.response.PostDetailApiResponse;

/**
 * 게시글 조회 관련 UseCase
 */
public interface FetchPostUseCase {

  /**
   * postId로 게시글 조회
   * @param postId
   * @return
   */
  PostDetailApiResponse getPost(String postId);


}
