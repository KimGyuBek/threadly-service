package com.threadly.controller.auth.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserLoginRequest {

  private String email;
  private String password;



}
