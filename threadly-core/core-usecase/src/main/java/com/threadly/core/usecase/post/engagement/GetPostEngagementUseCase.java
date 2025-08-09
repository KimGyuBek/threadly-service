package com.threadly.core.usecase.post.engagement;

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
  GetPostEngagementApiResponse getPostEngagement(GetPostEngagementQuery query);


}
