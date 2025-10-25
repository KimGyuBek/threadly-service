package com.threadly.core.port.token.out.command;

import java.time.Duration;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpsertRefreshTokenCommand {

  private String userId;
  private String refreshToken;
  private Duration duration;

}
