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
//  private String profileImageUrl;


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
   * @param userId
   * @param nickname
   * @param statusMessage
   * @param bio
   * @param phone
   * @param profileImage
   * @return
   */
  public void updateProfile(String nickname, String statusMessage,
      String bio, String phone, UserProfileImage userProfileImage) {
    this.nickname = nickname;
    this.statusMessage = statusMessage;
    this.bio = bio;
    this.phone = phone;
    this.userProfileImage = userProfileImage;
//    this.profileImageUrl = profileImageUrl;
  }

  /*테스트용*/
  @VisibleForTesting
  public static UserProfile newTestProfile(
      String userId,
      String nickname,
      String statusMessage,
      String bio,
      UserGenderType gender,
      UserProfileType profileType,
      String profileImageUrl
  ) {
    return
        UserProfile.builder()
            .userId(userId)
            .nickname(nickname)
            .statusMessage(statusMessage != null ? statusMessage : "")
            .bio(bio != null ? bio : "")
            .genderType(gender)
            .userProfileType(profileType)
            .userProfileImage(null)
            .build();
  }
}
