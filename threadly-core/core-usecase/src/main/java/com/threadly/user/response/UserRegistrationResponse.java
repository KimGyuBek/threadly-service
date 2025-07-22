package com.threadly.user.response;

import com.threadly.user.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserRegistrationResponse {

  private String userId;
  private String userName;
  private String email;
  private UserType userType;
  private boolean isActive;
  private boolean isEmailVerified;



}
