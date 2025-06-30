package com.threadly.user;

import com.google.common.annotations.VisibleForTesting;
import com.threadly.utils.RandomUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * UserProfile 도메인
 */
@Getter
@Builder
@AllArgsConstructor
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

  public static UserProfile newProfile(
      String nickname,
      String statusMessage,
      String bio,
      UserGenderType gender,
      UserProfileType profileType,
      String profileImageUrl
  ) {
    return
        UserProfile.builder()
            .userProfileId(RandomUtils.generateNanoId())
            .nickname(nickname)
            .statusMessage(statusMessage != null ? statusMessage : "")
            .bio(bio != null ? bio : "")
            .gender(gender)
            .profileType(profileType)
            .profileImageUrl(profileImageUrl != null ? profileImageUrl : "/")
            .build();
  }

  /*테스트용*/
  @VisibleForTesting
  public static UserProfile newTestProfile(
      String userProfileId,
      String nickname,
      String statusMessage,
      String bio,
      UserGenderType gender,
      UserProfileType profileType,
      String profileImageUrl
  ) {
    return
        UserProfile.builder()
            .userProfileId(userProfileId)
            .nickname(nickname)
            .statusMessage(statusMessage != null ? statusMessage : "")
            .bio(bio != null ? bio : "")
            .gender(gender)
            .profileType(profileType)
            .profileImageUrl(profileImageUrl != null ? profileImageUrl : "/")
            .build();
  }
}
