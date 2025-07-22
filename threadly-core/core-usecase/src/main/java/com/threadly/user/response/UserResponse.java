package com.threadly.user.response;

import com.threadly.user.UserType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

  private String userId;
  private String userName;
  private String password;
  private String email;
  private String phone;
  private UserType userType;
  private boolean isActive;
  private boolean isEmailVerified;
  private UserType type;

}
