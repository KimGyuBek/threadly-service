package com.threadly.core.port.auth.in.token.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "토큰 재발급 응답")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenReissueApiResponse {

  private String accessToken;
  private String refreshToken;

}
