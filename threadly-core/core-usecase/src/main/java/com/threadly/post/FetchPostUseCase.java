package com.threadly.post;

import com.threadly.post.query.GetPostQuery;
import com.threadly.post.response.PostDetailApiResponse;
import com.threadly.post.response.PostDetailListApiResponse;

/**
 * 게시글 조회 관련 UseCase
 */
public interface FetchPostUseCase {

  /**
   * postId로 게시글 조회
   * @param postId
   * @return
   */
  PostDetailApiResponse getPost(GetPostQuery query);

  /**
   * 사용자가 조회 가능한 게시글 목록 조회
   * @param userId
   *
   * @return
   */
  PostDetailListApiResponse getUserVisiblePostList(String userId);

}
