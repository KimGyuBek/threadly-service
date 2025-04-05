package com.threadly.token.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TokenResponse {

  private final String accessToken;
  private final String refreshToken;


}
