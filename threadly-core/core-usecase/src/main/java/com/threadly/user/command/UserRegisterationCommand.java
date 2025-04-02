package com.threadly.user.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRegisterationCommand {


  private String email;
  private String userName;
  private String password;
  private String phone;

  public UserRegisterationCommand(String email, String userName, String password, String phone) {
    this.email = email;
    this.userName = userName;
    this.password = password;
    this.phone = phone;
  }
}
