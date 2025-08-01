package com.threadly.user.profile.fetch;

import com.threadly.user.UserStatusType;

public interface MyProfileDetailsProjection {

  String getNickname();
  String getStatusMessage();
  String getBio();
  String getPhone();
  UserStatusType getStatus();

  String getProfileImageId();
  String getProfileImageUrl();
}
