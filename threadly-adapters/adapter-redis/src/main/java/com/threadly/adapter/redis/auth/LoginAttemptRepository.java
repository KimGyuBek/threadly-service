package com.threadly.adapter.redis.auth;

import com.threadly.core.port.auth.out.InsertLoginAttemptCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LoginAttemptRepository {

  private final RedisTemplate<String, Object> redisTemplate;

  public Integer getLoginAttemptCount(String userId) {
    return
        (Integer) redisTemplate.opsForValue().get(generateKey(userId));
  }

  public void increaseLoginAttempt(InsertLoginAttemptCommand insertLoginAttemptCommand) {
    String key = generateKey(insertLoginAttemptCommand.userId());

    int loginAttemptCount = insertLoginAttemptCommand.loginAttemptCount();

    /*업데이트*/
    redisTemplate.opsForValue().set(key, ++loginAttemptCount, insertLoginAttemptCommand.duration());
  }

  public void deleteLoginAttempt(String userId) {
    redisTemplate.delete(generateKey(userId));
  }

  private String generateKey(String userId) {
    return "login:attempt:" + userId;
  }

}
