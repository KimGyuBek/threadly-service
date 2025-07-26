package com.threadly.user;

import com.google.common.annotations.VisibleForTesting;
import com.threadly.user.profile.UserProfile;
import com.threadly.utils.RandomUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * User 도메인
 */
@Getter
@Builder
@AllArgsConstructor
public class User {

  private String userId;
  private String userName;
  private String password;
  private String email;
  private String phone;
  private UserType userType;
  private UserStatusType userStatusType;
  private boolean isEmailVerified;

  private UserProfile userProfile;

  /*email 인증*/
  public void verifyEmail() {
    isEmailVerified = true;
  }

  /*검증*/
  public boolean isEmailVerified() {
    return isEmailVerified;
  }

  public boolean hasUserProfile() {
    return userProfile != null;
  }

  /**
   * 새로운 사용자 생성
   *
   * @param userName
   * @param password
   * @param email
   * @param phone
   * @return
   */
  public static User newUser(String userName, String password, String email, String phone) {
    return
        User.builder()
            .userId(RandomUtils.generateNanoId())
            .userName(userName)
            .password(password)
            .email(email)
            .phone(phone)
            .userType(UserType.USER)
            .userStatusType(UserStatusType.ACTIVE)
            .build();
  }

  /**
   * userId만 포함한 user 도메인 객체 생성
   *
   * @param userId
   * @return
   */
  public static User of(String userId) {
    return User.builder()
        .userId(userId)
        .build();
  }

  /*상태 변경*/

  /**
   * ACTIVE 상태로 변경
   */
  void markAsActive() {
    this.userStatusType = UserStatusType.ACTIVE;
  }

  /**
   * DELETED 상태로 변경
   */
  void markAsDeleted() {
    this.userStatusType = UserStatusType.DELETED;
  }

  /**
   * INACTIVE 상태로 변경
   */
  void markAsInactive() {
    this.userStatusType = UserStatusType.INACTIVE;
  }


  /**
   * BANNED 상태로 변경
   */
  void markAsBanned() {
    this.userStatusType = UserStatusType.BANNED;
  }

  /*UserProfile*/

  /**
   * 사용자 프로필 생성
   *
   * @param nickname
   * @param statusMessage
   * @param bio
   * @param phone
   * @param genderType
   * @return
   */
  public UserProfile setUserProfile(String nickname, String statusMessage, String bio, String phone,
      UserGenderType genderType) {
    return UserProfile.setProfile(
        this.userId,
        nickname,
        statusMessage,
        bio,
        phone,
        genderType
    );
  }

  /**
   * 프로필 업데이트
   *
   * @param nickname
   * @param statusMessage
   * @param bio
   * @param phone
   * @param profileImageUrl
   */
  public void updateProfile(String nickname, String statusMessage, String bio, String phone,
      String profileImageUrl) {
    this.userProfile.updateProfile(nickname, statusMessage, bio, phone, profileImageUrl);
  }

  public void setUserProfile(UserProfile userProfile) {
    this.userProfile = userProfile;
  }

  public String getNickname() {
    return userProfile.getNickname();
  }

  public String getStatusMessage() {
    return userProfile.getStatusMessage();
  }

  public String getBio() {
    return userProfile.getBio();
  }

  public UserGenderType getGender() {
    return userProfile.getGenderType();
  }

  public String getProfileImageUrl() {
    return userProfile.getProfileImageUrl();
  }

  /*테스트용 메서드*/

  /**
   * 테스트용 User 도메인 생성
   *
   * @param userId
   * @param userName
   * @param password
   * @param email
   * @param phone
   * @return
   */
  @VisibleForTesting
  public static User newTestUser(String userId, String userName, String password, String email,
      String phone) {
    return
        User.builder()
            .userId(userId)
            .userName(userName)
            .password(password)
            .email(email)
            .phone(phone)
            .userType(UserType.USER)
            .userStatusType(UserStatusType.ACTIVE)
            .build();
  }

  /**
   * 테스트용 이메일 인증 상태로 변경
   */
  @VisibleForTesting
  public void setEmailVerified() {
    isEmailVerified = true;
  }
}
