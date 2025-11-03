package com.threadly.adapter.redis.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.threadly.RedisTestApplication;
import com.threadly.adapter.redis.auth.LoginAttemptRedisAdapter;
import com.threadly.core.port.auth.out.InsertLoginAttemptCommand;
import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * LoginAttemptRepository Test
 */
@ActiveProfiles("test")
@SpringBootTest(classes = {RedisTestApplication.class})
class LoginAttemptRedisAdapterTest {

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  @Autowired
  private LoginAttemptRedisAdapter loginAttemptRedisAdapter;

  @DisplayName("이미 존재하는 값이 있는 경우")
  @Test
  public void getLoginAttemptCount() throws Exception {
    //given
    String key = "login:attempt:" + "1";

    redisTemplate.opsForValue().set(key, 2);

    //when
    int result = loginAttemptRedisAdapter.getLoginAttemptCount("1");

    //then
    assertThat(result).isEqualTo(2);

  }

  @DisplayName("이미 존재하는 값이 없는 경우 - null 리턴")
  @Test
  public void getLoginAttemptCount_shouldReturnNull_whenKeyNotExists() throws Exception {
    //given
    String key = "login:attempt:" + "1";

    redisTemplate.opsForValue().set(key, 2);

    //when
    Integer result = loginAttemptRedisAdapter.getLoginAttemptCount("2");

    //then
    assertNull(result);
  }

  @DisplayName("이미 존재하는 값이 있는 경우")
  @Test
  public void increaseLoginAttempt_shouldReturnIncreasedValue_whenKeyExists() throws Exception {
    //given
    String key = "login:attempt:" + "1";
    redisTemplate.opsForValue().set(key, 1);

    Integer count = (Integer) redisTemplate.opsForValue().get(key);
    //when
    loginAttemptRedisAdapter.increaseLoginAttempt(
        new InsertLoginAttemptCommand(
            "1",
            count,
            Duration.ofSeconds(3)
        ));

    int result = loginAttemptRedisAdapter.getLoginAttemptCount("1");

    //then
    assertThat(result).isEqualTo(2);

  }

  @DisplayName("로그인 시도 횟수 TTL 만료 후 조회 - null 리턴")
  @Test
  public void getLoginAttemptCount_shouldReturnNull_whenTtlExpired() throws Exception {
    //given
    String userId = "user1";
    String key = "login:attempt:" + userId;
    Duration shortTtl = Duration.ofSeconds(2);

    redisTemplate.opsForValue().set(key, 3, shortTtl);

    //when
    /*TTL 만료 대기*/
    Thread.sleep(2500);

    Integer result = loginAttemptRedisAdapter.getLoginAttemptCount(userId);

    //then
    assertNull(result);
  }
}