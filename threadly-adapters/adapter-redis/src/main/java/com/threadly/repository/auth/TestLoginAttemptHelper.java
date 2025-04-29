package com.threadly.repository.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * 테스트시 사용하는 Redis helper
 */
@Profile("test")
@Repository
@RequiredArgsConstructor
public class TestLoginAttemptHelper {

  private final RedisTemplate<String, Object> redisTemplate;

  public void insertLoginAttempt(String userId, int attempts) {
    String key = "login:attempt:" + userId;

    redisTemplate.opsForValue().set(key, attempts);
  }

  public void clearRedis() {
    redisTemplate.getConnectionFactory().getConnection().flushAll();
  }

  public Integer getLoginAttemptCount(String userId) {
    String key = "login:attempt:" + userId;
    return
        (Integer) redisTemplate.opsForValue().get(key);
  }

}
