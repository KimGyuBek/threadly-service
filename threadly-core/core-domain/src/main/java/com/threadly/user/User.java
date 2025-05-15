package com.threadly.user;

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
  private boolean isActive;
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


  private User(String userName, String password, String email, String phone,
      UserType userType) {
    this.userId = null;
    this.userName = userName;
    this.password = password;
    this.email = email;
    this.phone = phone;
    this.userType = userType;
    this.isActive = true;
    this.isEmailVerified = false;
    this.userProfile = null;
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
            .userName(userName)
            .password(password)
            .email(email)
            .phone(phone)
            .userType(UserType.USER)
            .build();
  }

  /*User Profile*/

  public void setUserProfile(UserProfile userProfile) {
    this.userProfile = userProfile;
  }

  /**
   * UserProfile 생성
   *
   * @param nickname
   * @param statusMessage
   * @param bio
   * @param gender
   * @param profileImageUrl
   * @param profileType
   */
  public void setProfile(
      String nickname,
      String statusMessage,
      String bio,
      UserGenderType gender,
      String profileImageUrl,
      UserProfileType profileType
  ) {
    this.userProfile = UserProfile.create(
        nickname,
        statusMessage,
        bio,
        gender,
        profileType,
        profileImageUrl
    );
  }

  /**
   * userProfile 업데이트
   * @param nickname
   * @param statusMessage
   * @param bio
   * @param gender
   * @param profileImageUrl
   */
  public void updateUserProfile(String nickname, String statusMessage, String bio,
      UserGenderType gender, String profileImageUrl) {
    userProfile.updateProfile(nickname, statusMessage, bio, gender, profileImageUrl);
  }

  public String getUserProfileId() {
    return userProfile.getUserProfileId();
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
    return userProfile.getGender();
  }

  public UserProfileType getProfileType() {
    return userProfile.getProfileType();
  }

  public String getProfileImageUrl() {
    return userProfile.getProfileImageUrl();
  }

}
