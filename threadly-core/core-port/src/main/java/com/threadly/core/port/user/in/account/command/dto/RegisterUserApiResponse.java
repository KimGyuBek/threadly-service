package com.threadly.core.port.user.in.account.command.dto;

import com.threadly.core.domain.user.UserStatusType;
import com.threadly.core.domain.user.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 사용자 회원가입 api 응답 객체
 */

@Getter
@AllArgsConstructor
@Builder
public class RegisterUserApiResponse {

  private String userId;
  private String userName;
  private String email;
  private UserType userType;
  private UserStatusType userStatusType;
  private boolean isEmailVerified;



}
