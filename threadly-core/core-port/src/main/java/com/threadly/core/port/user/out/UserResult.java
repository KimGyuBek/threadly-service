package com.threadly.core.port.user.out;

import com.threadly.core.domain.user.UserStatus;
import com.threadly.core.domain.user.UserRoleType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResult {

  private String userId;

  private String userName;

  private String password;

  private String email;

  private String phone;

  private UserRoleType userRoleType;

  private UserStatus userStatus;

  private boolean isEmailVerified;


}
