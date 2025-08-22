package com.threadly.core.usecase.follow.query;

import com.threadly.core.usecase.follow.query.dto.GetFollowRequestsQuery;
import com.threadly.core.usecase.follow.query.dto.GetUserFollowStatsApiResponse;
import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.usecase.follow.query.dto.GetFollowersQuery;
import com.threadly.core.usecase.follow.query.dto.GetFollowingsQuery;

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

  /**
   * 주어진 userId에 해당하는 사용자의 팔로워, 팔로잉 수 조회
   * @param userId
   * @return
   */
  GetUserFollowStatsApiResponse getUserFollowStats(String userId);
}
