package com.threadly.user.get;

import com.threadly.user.UserStatusType;
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
  private UserStatusType userStatusType;
  private boolean isEmailVerified;
  private UserType type;

}
