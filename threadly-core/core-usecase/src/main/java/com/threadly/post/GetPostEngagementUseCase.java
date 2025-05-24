package com.threadly.post;

import com.threadly.post.query.GetPostEngagementQuery;
import com.threadly.post.response.PostEngagementApiResponse;

/**
 * 게시물 활동 관련 조회 UseCase
 */
public interface GetPostEngagementUseCase {

  /**
   * 게시글 활동 조회
   *
   * @param query
   * @return
   */
  PostEngagementApiResponse getPostEngagement(GetPostEngagementQuery query);


}
