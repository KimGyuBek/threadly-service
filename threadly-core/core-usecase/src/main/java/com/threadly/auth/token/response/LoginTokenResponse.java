package com.threadly.auth.token.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginTokenResponse {

  private final String accessToken;
  private final String refreshToken;


}
