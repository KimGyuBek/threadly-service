package com.threadly.token;

import java.time.Duration;
import lombok.Builder;
import lombok.Getter;

/**
 * BlackList token을 저장하기 위한 DTO
 */

@Getter
@Builder
public class InsertBlackListToken
{
  private String userId;
  private String accessToken;
  private Duration duration;


}
