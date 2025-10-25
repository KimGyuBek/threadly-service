package com.threadly.core.port.token.out.command;

import java.time.Duration;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InsertRefreshTokenCommand {

  private String refreshToken;
  private String userId;
  private Duration duration;




}
