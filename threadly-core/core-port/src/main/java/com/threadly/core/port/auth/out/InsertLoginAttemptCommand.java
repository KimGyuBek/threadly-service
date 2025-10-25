package com.threadly.core.port.auth.out;

import java.time.Duration;
import lombok.Builder;
import lombok.Getter;

/**
 * LoginAttempt 삽입용 DTO
 */
public record InsertLoginAttemptCommand (
    String userId,
    int loginAttemptCount,
    Duration duration
){
}
