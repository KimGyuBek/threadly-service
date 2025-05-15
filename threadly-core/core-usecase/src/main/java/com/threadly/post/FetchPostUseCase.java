package com.threadly.post;

import com.threadly.post.response.PostDetailApiResponse;
import com.threadly.post.response.PostDetailListApiResponse;
import java.util.List;

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

  /**
   * 게시글 리스트 조회
   * @return
   */
  PostDetailListApiResponse getPostList();

}
