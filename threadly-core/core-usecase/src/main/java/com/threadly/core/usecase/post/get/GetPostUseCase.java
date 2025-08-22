package com.threadly.core.usecase.post.get;

import com.threadly.commons.response.CursorPageApiResponse;

/**
 * 게시글 조회 관련 UseCase
 */
public interface GetPostUseCase {

  /**
   * postId로 게시글 조회
   * @param postId
   * @return
   */
  PostDetails getPost(GetPostQuery query);

  /**
   * 사용자가 조회 가능한 게시글 목록 조회
   * @param query
   *
   * @return
   */
  CursorPageApiResponse getUserVisiblePostListByCursor(GetPostListQuery query);

}
