package com.threadly.user.register;

import com.threadly.user.UserStatusType;
import com.threadly.user.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 사용자 회원가입 api 응답 객체
 */

@Getter
@AllArgsConstructor
@Builder
public class UserRegistrationApiResponse {

  private String userId;
  private String userName;
  private String email;
  private UserType userType;
  private UserStatusType userStatusType;
  private boolean isEmailVerified;



}
