package com.threadly.core.port.user.follow;

import java.time.LocalDateTime;

/**
 * 팔로워 목록 조회 projection 객체
 */
public interface FollowerProjection {
  String getFollowerId();
  String getFollowerNickname();
  String getFollowerProfileImageUrl();
  LocalDateTime getFollowedAt();

}
