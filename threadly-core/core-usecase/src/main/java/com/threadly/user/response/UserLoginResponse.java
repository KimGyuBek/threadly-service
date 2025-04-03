package com.threadly.user.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserLoginResponse {

  private String userId;
  private String userName;
  private String email;
  private String userType;

}
