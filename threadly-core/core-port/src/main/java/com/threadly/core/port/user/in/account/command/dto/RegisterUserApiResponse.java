package com.threadly.core.port.user.in.account.command.dto;

import com.threadly.core.domain.user.UserStatus;
import com.threadly.core.domain.user.UserRoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 사용자 회원가입 api 응답 객체
 */
@Schema(description = "회원가입 응답")
@Getter
@AllArgsConstructor
@Builder
public class RegisterUserApiResponse {

  private String userId;
  private String userName;
  private String email;
  private UserRoleType userRoleType;
  private UserStatus userStatus;
  private boolean isEmailVerified;



}
