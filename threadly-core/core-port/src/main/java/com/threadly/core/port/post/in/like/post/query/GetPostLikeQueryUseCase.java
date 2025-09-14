package com.threadly.core.port.post.in.like.post.query;

import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.port.post.in.like.post.query.dto.GetPostLikersQuery;

/**
 * 게시글 좋아요 누른 사용자 조회 관련 UseCase
 */
public interface GetPostLikeQueryUseCase {

  /**
   * 게시글에 좋아요를 누른 사용자 조회
   * @param query
   * @return
   */
  CursorPageApiResponse getPostLikers(GetPostLikersQuery query);



}
