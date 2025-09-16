package com.threadly.adapter.redis.repository.auth;

import com.threadly.core.port.auth.out.InsertLoginAttemptCommand;
import com.threadly.core.port.auth.out.LoginAttemptCommandPort;
import com.threadly.core.port.auth.out.LoginAttemptQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * 로그인 시도 제한 repository
 */
@Repository
@RequiredArgsConstructor
public class LoginAttemptCommandQueryRepository implements LoginAttemptQueryPort,
    LoginAttemptCommandPort {

  private final RedisTemplate<String, Object> redisTemplate;


  @Override
  public Integer getLoginAttemptCount(String userId) {
    return
        (Integer) redisTemplate.opsForValue().get(generateKey(userId));
  }

  @Override
  public void increaseLoginAttempt(InsertLoginAttemptCommand insertLoginAttemptCommand) {
    String key = generateKey(insertLoginAttemptCommand.userId());

    int loginAttemptCount = insertLoginAttemptCommand.loginAttemptCount();

    /*업데이트*/
    redisTemplate.opsForValue().set(key, ++loginAttemptCount, insertLoginAttemptCommand.duration());
  }

  @Override
  public void deleteLoginAttempt(String userId) {
    redisTemplate.delete(generateKey(userId));
  }

  private String generateKey(String userId) {
    return "login:attempt:" + userId;
  }

}
