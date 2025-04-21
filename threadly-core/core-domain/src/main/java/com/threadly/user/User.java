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
    return new User(
        userName,
        password,
        email,
        phone,
        UserType.USER
    );
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
  }

  /*email 인증*/
  public void verifyEmail() {
    isEmailVerified = true;
  }

  /*검증*/
  public boolean isEmailVerified() {
    return isEmailVerified;
  }
}
