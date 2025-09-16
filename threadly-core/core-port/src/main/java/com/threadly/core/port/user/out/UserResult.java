package com.threadly.core.port.user.out;

import com.threadly.core.domain.user.UserStatusType;
import com.threadly.core.domain.user.UserType;
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

  private UserType userType;

  private UserStatusType userStatusType;

  private boolean isEmailVerified;


}
