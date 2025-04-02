package com.threadly.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserRegisterRequest {

  private String email;
  private String userName;
  private String password;
  private String phone;

}
