package com.threadly.core.port.follow.out;

/**
 * 사용자의 팔로워, 팔로잉 수 조회 프로젝션
 */
public interface UserFollowStatsProjection {
  int getFollowerCount();

  int getFollowingCount();

}
