package com.threadly.user;

import lombok.Builder;
import lombok.Getter;

/**
 * UserProfile 도메인
 */
@Getter
@Builder
public class UserProfile {

  private String userProfileId;
  private String nickname;
  private String statusMessage;
  private String bio;
  private UserGenderType gender;
  private UserProfileType profileType;
  private String profileImageUrl;

  public void updateProfile(String nickname, String statusMessage, String bio,
      UserGenderType gender,
      String profileImageUrl) {
    this.nickname = nickname;
    this.statusMessage = statusMessage;
    this.bio = bio;
    this.gender = gender;
    this.profileImageUrl = profileImageUrl;
  }

  public static UserProfile create(
      String nickname,
      String statusMessage,
      String bio,
      UserGenderType gender,
      UserProfileType profileType,
      String profileImageUrl
  ) {
    return
        UserProfile.builder()
            .userProfileId(null)
            .nickname(nickname)
            .statusMessage(statusMessage != null ? statusMessage : "")
            .bio(bio != null ? bio : "")
            .gender(gender)
            .profileType(profileType)
            .profileImageUrl(profileImageUrl != null ? profileImageUrl : "/")
            .build();
  }

}
