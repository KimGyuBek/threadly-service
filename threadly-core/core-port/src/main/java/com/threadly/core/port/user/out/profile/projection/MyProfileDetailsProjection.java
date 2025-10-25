package com.threadly.core.port.user.out.profile.projection;

import com.threadly.core.domain.user.UserStatus;

public interface MyProfileDetailsProjection {

  String getNickname();
  String getStatusMessage();
  String getBio();
  String getPhone();
  UserStatus getStatus();

  String getProfileImageId();
  String getProfileImageUrl();
}
