package com.threadly.core.usecase.auth.token.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenReissueApiResponse {

  private String accessToken;
  private String refreshToken;

}
