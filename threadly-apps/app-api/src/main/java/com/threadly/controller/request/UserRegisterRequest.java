package com.threadly.controller.request;

import com.threadly.PasswordEncryption;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserRegisterRequest {

  private String email;
  private String userName;

  @PasswordEncryption
  private String password;
  private String phone;

}
