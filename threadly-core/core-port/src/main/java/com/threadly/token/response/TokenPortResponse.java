package com.threadly.token.response;

import com.threadly.user.UserType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TokenPortResponse {

  private String tokenId;
  private String userId;
  private UserType userType;

  /*TODO 필요 없지 않을까? 어쩌피 새로 업데이트 해주는데*/
  private String accessToken;
  private String refreshToken;
  private LocalDateTime accessTokenExpiresAt;
  private LocalDateTime refreshTokenExpiresAt;

}
