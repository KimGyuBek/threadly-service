package com.threadly.follow.query;

import com.threadly.follow.query.dto.GetFollowRequestsApiResponse;
import com.threadly.follow.query.dto.GetFollowRequestsQuery;
import com.threadly.follow.query.dto.GetFollowersApiResponse;
import com.threadly.follow.query.dto.GetFollowersQuery;
import com.threadly.follow.query.dto.GetFollowingsApiResponse;
import com.threadly.follow.query.dto.GetFollowingsQuery;

/**
 * follow 요청 조회 관련 usecase
 */
public interface FollowQueryUseCase {

  /**
   * 커서 기반 팔로우 요청 목록 조회 usecase
   * @param query
   * @return
   */
  GetFollowRequestsApiResponse getFollowRequestsByCursor(GetFollowRequestsQuery query);

  /**
   * 커서 기반 팔로워 목록 조회
   * @param query
   * @return
   */
  GetFollowersApiResponse getFollowers(GetFollowersQuery query);

  /**
   * 커서 기반 팔로잉 목록 조회
   * @param query
   * @return
   */
  GetFollowingsApiResponse getFollowings(GetFollowingsQuery query);
}
