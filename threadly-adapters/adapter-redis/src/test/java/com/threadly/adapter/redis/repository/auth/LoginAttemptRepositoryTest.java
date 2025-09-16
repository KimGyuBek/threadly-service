package com.threadly.adapter.redis.repository.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.threadly.RedisTestApplication;
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
class LoginAttemptRepositoryTest {

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  @Autowired
  private LoginAttemptCommandQueryRepository loginAttemptRepository;

  @DisplayName("이미 존재하는 값이 있는 경우")
  @Test
  public void getLoginAttemptCount() throws Exception {
    //given
    String key = "login:attempt:" + "1";

    redisTemplate.opsForValue().set(key, 2);

    //when
    int result = loginAttemptRepository.getLoginAttemptCount("1");

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
    Integer result = loginAttemptRepository.getLoginAttemptCount("2");

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
    loginAttemptRepository.increaseLoginAttempt(
        new InsertLoginAttemptCommand(
            "1",
            count,
            Duration.ofSeconds(3)
        ));

    int result = loginAttemptRepository.getLoginAttemptCount("1");

    //then
    assertThat(result).isEqualTo(2);

  }
}