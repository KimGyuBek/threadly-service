package com.threadly.core.port.user.out.profile.projection;

import com.threadly.core.domain.user.UserStatus;

/**
 * 사용자 프로필 정보 조회 projection 객체
 */
public interface UserProfileProjection {

  String getUserId();

  String getNickname();

  String getStatusMessage();

  String getBio();

  String getPhone();

  String getProfileImageUrl();

  UserStatus getUserStatus();

  boolean getIsPrivate();

}
