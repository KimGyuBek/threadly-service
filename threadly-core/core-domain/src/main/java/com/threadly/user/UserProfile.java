package com.threadly.user;

import com.google.common.annotations.VisibleForTesting;
import com.threadly.utils.RandomUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 사용자 프로필 도메인
 */
@Getter
@AllArgsConstructor
@Builder
public class UserProfile {

  //  private String userId;
  private String userProfileId;
  private String nickname;
  private String statusMessage;
  private String bio;
  private String phone;
  private UserGenderType genderType;
  private UserProfileType userProfileType;
  private String profileImageUrl;
//  ProfileImage profileImage;


  /**
   * 프로필 설정
   *
   * @param phone
   * @param nickname
   * @param statusMessage
   * @param bio
   * @return
   */
  public static UserProfile setProfile(String nickname, String statusMessage, String bio, String phone,
      UserGenderType genderType ) {
    return new UserProfile(
        RandomUtils.generateNanoId(),
        nickname,
        statusMessage,
        bio,
        phone,
        genderType,
        UserProfileType.USER,
        "/"
    );
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
            .genderType(gender)
            .userProfileType(profileType)
            .profileImageUrl(profileImageUrl != null ? profileImageUrl : "/")
            .build();
  }
}
