package com.threadly.adapter.redis.repository.auth;

import com.threadly.core.port.auth.out.DeleteLoginAttemptPort;
import com.threadly.core.port.auth.out.FetchLoginAttemptPort;
import com.threadly.core.port.auth.out.InsertLoginAttempt;
import com.threadly.core.port.auth.out.UpsertLoginAttemptPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * 로그인 시도 제한 repository
 */
@Repository
@RequiredArgsConstructor
public class LoginAttemptRepository implements FetchLoginAttemptPort, UpsertLoginAttemptPort,
    DeleteLoginAttemptPort {

  private final RedisTemplate<String, Object> redisTemplate;

  @Override
  public boolean isLoginBlocked(String userId) {
    return false;
  }


  @Override
  public Integer getLoginAttemptCount(String userId) {
    return
        (Integer) redisTemplate.opsForValue().get(generateKey(userId));
  }

  @Override
  public void increaseLoginAttempt(InsertLoginAttempt insertLoginAttempt) {
    String key = generateKey(insertLoginAttempt.getUserId());

    int loginAttemptCount = insertLoginAttempt.getLoginAttemptCount();

    /*업데이트*/
    redisTemplate.opsForValue().set(key, ++loginAttemptCount, insertLoginAttempt.getDuration());
  }

  @Override
  public void deleteLoginAttempt(String userId) {
    redisTemplate.delete(generateKey(userId));
  }

  private String generateKey(String userId) {
    return "login:attempt:" + userId;
  }

}
