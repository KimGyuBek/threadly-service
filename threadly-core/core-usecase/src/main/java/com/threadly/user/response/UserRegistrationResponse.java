package com.threadly.user.response;

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
  private String userType;



}
