package com.threadly.post.like;

import com.threadly.post.like.query.GetPostLikersQuery;
import com.threadly.post.like.response.PostLikersApiResponse;

/**
 * 게시글 좋아요 누른 사용자 조회 관련 UseCase
 */
public interface GetPostLikersUseCase {

  /**
   * 게시글에 좋아요를 누른 사용자 조회
   * @param query
   * @return
   */
  PostLikersApiResponse getPostLikers(GetPostLikersQuery query);



}
