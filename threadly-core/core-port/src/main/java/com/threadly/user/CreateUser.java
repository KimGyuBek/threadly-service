package com.threadly.user;

import lombok.Getter;

@Getter
public class CreateUser {

  private String email;
  private String userName;
  private String password;
  private String phone;

  public CreateUser(String email, String userName, String password, String phone) {
    this.email = email;
    this.userName = userName;
    this.password = password;
    this.phone = phone;
  }
}
