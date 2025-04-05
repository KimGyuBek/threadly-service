package com.threadly.token;

import com.threadly.user.UserType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateToken {

//  private String tokenId;
  private String userId;
  private String accessToken;
  private String refreshToken;
  private UserType userType;
  private LocalDateTime accessTokenExpiresAt;
  private LocalDateTime refreshTokenExpiresAt;


}
