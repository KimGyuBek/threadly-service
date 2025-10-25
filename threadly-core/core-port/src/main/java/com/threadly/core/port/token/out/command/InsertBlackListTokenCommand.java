package com.threadly.core.port.token.out.command;

import java.time.Duration;
import lombok.Builder;
import lombok.Getter;

/**
 * BlackList token을 저장하기 위한 DTO
 */

@Getter
@Builder
public class InsertBlackListTokenCommand
{
  private String userId;
  private String accessToken;
  private Duration duration;


}
