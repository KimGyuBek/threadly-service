package com.threadly.auth.token.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenReissueResponse {

  private String accessToken;
  private String refreshToken;

}
