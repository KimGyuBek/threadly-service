package com.threadly.core.port.user.out.profile.query;

import com.threadly.core.domain.user.UserStatusType;

public interface MyProfileDetailsProjection {

  String getNickname();
  String getStatusMessage();
  String getBio();
  String getPhone();
  UserStatusType getStatus();

  String getProfileImageId();
  String getProfileImageUrl();
}
