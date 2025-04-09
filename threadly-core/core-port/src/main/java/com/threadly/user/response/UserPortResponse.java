package com.threadly.user.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserPortResponse {

  private String userId;

  private String userName;

  private String password;

  private String email;

  private String phone;

  private String userType;

  private boolean isActive;

  private boolean isEmailVerified;


}
