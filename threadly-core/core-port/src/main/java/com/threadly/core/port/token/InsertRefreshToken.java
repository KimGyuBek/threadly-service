package com.threadly.core.port.token;

import java.time.Duration;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InsertRefreshToken {

  private String refreshToken;
  private String userId;
  private Duration duration;




}
