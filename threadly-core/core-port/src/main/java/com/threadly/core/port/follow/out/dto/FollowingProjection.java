package com.threadly.core.port.follow.out.dto;

import java.time.LocalDateTime;

/**
 * 팔로잉 목록 조회 projection 객체
 */
public interface FollowingProjection {
  String getFollowingId();
  String getFollowingNickname();
  String getFollowingProfileImageUrl();
  LocalDateTime getFollowedAt();

}
