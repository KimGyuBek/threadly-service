package com.threadly.user.account.command.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 사용자 회원가입 command
 */
@Getter
@Builder
public class RegisterUserCommand {


  private String email;
  private String userName;
  private String password;
  private String phone;

  public RegisterUserCommand(String email, String userName, String password, String phone) {
    this.email = email;
    this.userName = userName;
    this.password = password;
    this.phone = phone;
  }
}
