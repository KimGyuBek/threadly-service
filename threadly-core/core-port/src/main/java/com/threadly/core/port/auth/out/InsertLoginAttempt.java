package com.threadly.core.port.auth.out;

import java.time.Duration;
import lombok.Builder;
import lombok.Getter;

/**
 * LoginAttempt 삽입용 DTO
 */
@Getter
@Builder
public class InsertLoginAttempt {

  private String userId;
  private int loginAttemptCount;

  private Duration duration;

}
