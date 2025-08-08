package com.threadly.follow.query;

import com.threadly.follow.query.dto.GetFollowRequestsQuery;
import com.threadly.response.CursorPageApiResponse;
import com.threadly.follow.query.dto.GetFollowersQuery;
import com.threadly.follow.query.dto.FollowingApiResponse;
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
  CursorPageApiResponse getFollowRequestsByCursor(GetFollowRequestsQuery query);

  /**
   * 커서 기반 팔로워 목록 조회
   * @param query
   * @return
   */
  CursorPageApiResponse getFollowers(GetFollowersQuery query);

  /**
   * 커서 기반 팔로잉 목록 조회
   * @param query
   * @return
   */
  CursorPageApiResponse getFollowings(GetFollowingsQuery query);
}
