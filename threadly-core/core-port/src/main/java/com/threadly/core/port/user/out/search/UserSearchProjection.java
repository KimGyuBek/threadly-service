package com.threadly.core.port.user.out.search;

import com.threadly.core.domain.follow.FollowStatus;

/**
 * 사용자 검색 projection 객체
 */
public interface UserSearchProjection {

  /*UserPreview*/
  String getUserId();
  String getUserNickname();
  String getUserProfileImageUrl();

  FollowStatus getFollowStatus();
}
