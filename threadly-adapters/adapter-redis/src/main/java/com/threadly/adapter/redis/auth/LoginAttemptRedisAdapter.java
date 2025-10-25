package com.threadly.adapter.redis.auth;

import com.threadly.core.port.auth.out.InsertLoginAttemptCommand;
import com.threadly.core.port.auth.out.LoginAttemptCommandPort;
import com.threadly.core.port.auth.out.LoginAttemptQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * 로그인 시도 제한 repository
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class LoginAttemptRedisAdapter implements LoginAttemptQueryPort,
    LoginAttemptCommandPort {

  private final LoginAttemptRepository loginAttemptRepository;

  @Override
  public void increaseLoginAttempt(InsertLoginAttemptCommand insertLoginAttemptCommand) {
    loginAttemptRepository.increaseLoginAttempt(insertLoginAttemptCommand);
  }

  @Override
  public void deleteLoginAttempt(String userId) {
    loginAttemptRepository.deleteLoginAttempt(userId);
  }

  @Override
  public Integer getLoginAttemptCount(String userId) {
    return loginAttemptRepository.getLoginAttemptCount(userId);
  }
}
