package com.threadly.user.profile.fetch;

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

}
