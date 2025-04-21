package com.threadly.token;

import java.time.Duration;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpsertRefreshToken {

  private String userId;
  private String refreshToken;
  private Duration duration;

}
