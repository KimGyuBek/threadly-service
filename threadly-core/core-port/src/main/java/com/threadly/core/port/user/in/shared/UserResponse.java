package com.threadly.core.port.user.in.shared;

import com.threadly.core.domain.user.UserStatus;
import com.threadly.core.domain.user.UserRoleType;
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
  private UserRoleType userRoleType;
  private UserStatus userStatus;
  private boolean isEmailVerified;
  private UserRoleType type;

}
