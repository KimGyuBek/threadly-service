package com.threadly.user.profile;

import com.google.common.annotations.VisibleForTesting;
import com.threadly.user.UserGenderType;
import com.threadly.user.profile.image.UserProfileImage;
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

  private String userId;
  private String nickname;
  private String statusMessage;
  private String bio;
  private String phone;
  private UserGenderType genderType;
  private UserProfileType userProfileType;
  private UserProfileImage userProfileImage;


  /**
   * 프로필 설정
   *
   * @param phone
   * @param nickname
   * @param statusMessage
   * @param bio
   * @return
   */
  public static UserProfile setProfile(String userId, String nickname, String statusMessage,
      String bio, String phone,
      UserGenderType genderType) {
    return new UserProfile(
        userId,
        nickname,
        statusMessage,
        bio,
        phone,
        genderType,
        UserProfileType.USER,
        null
    );
  }

  /**
   * 프로필 업데이트
   *
   * @param nickname
   * @param statusMessage
   * @param bio
   * @param phone
   * @return
   */
  public void updateProfile(String nickname, String statusMessage,
      String bio, String phone) {
    this.nickname = nickname;
    this.statusMessage = statusMessage;
    this.bio = bio;
    this.phone = phone;

//    /* 새로운 프로필 이미지일경우*/
//    if(userProfileImageId != null) {
//      this.userProfileImage.setProfileImage(userProfileImageId);
//    }
  }

  /**
   * 주어진 userProfileImageId를 새로운 프로필 이미지로 설정
   * @param userProfileImageId
   */
  public void updateProfileImage(String userProfileImageId) {
    this.userProfileImage.setProfileImage(userProfileImageId);
  }

  /*테스트용*/
  @VisibleForTesting
  public static UserProfile newTestProfile(
      String userId,
      String nickname,
      String statusMessage,
      String bio,
      UserGenderType gender,
      UserProfileType profileType
  ) {
    return
        UserProfile.builder()
            .userId(userId)
            .nickname(nickname)
            .statusMessage(statusMessage != null ? statusMessage : "")
            .bio(bio != null ? bio : "")
            .genderType(gender)
            .userProfileType(profileType)
            .build();
  }
}
