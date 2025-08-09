package com.threadly.core.port.user.follow;

import java.time.LocalDateTime;

/**
 * 팔로우 요청 목록 조회 projection 객체
 */
public interface FollowRequestsProjection {

  String getFollowId();

  String getRequesterId();

  String getRequesterNickname();

  String getRequesterProfileImageUrl();

  LocalDateTime getFollowRequestedAt();


}
