package com.threadly.adapter.redis.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.threadly.RedisTestApplication;
import com.threadly.commons.exception.mail.EmailVerificationException;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * EmailVerificationRedisAdapter Test
 */
@ActiveProfiles("test")
@SpringBootTest(classes = {RedisTestApplication.class})
class EmailVerificationRedisAdapterTest {

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  @Autowired
  private EmailVerificationRedisAdapter emailVerificationRedisAdapter;

  @BeforeEach
  void clearRedis() {
    redisTemplate.getConnectionFactory().getConnection().flushAll();
  }

  @DisplayName("이메일 인증 코드 저장 후 조회 성공")
  @Test
  public void getUserId_shouldReturnUserId_whenCodeExists() throws Exception {
    //given
    String userId = "user1";
    String code = "verificationCode123";
    Duration duration = Duration.ofSeconds(5);

    emailVerificationRedisAdapter.saveCode(userId, code, duration);

    //when
    String result = emailVerificationRedisAdapter.getUserId(code);

    //then
    assertThat(result).isEqualTo(userId);
  }

  @DisplayName("존재하지 않는 코드 조회 시 예외 발생")
  @Test
  public void getUserId_shouldThrowException_whenCodeNotExists() throws Exception {
    //given
    String code = "nonExistentCode";

    //when & then
    assertThrows(EmailVerificationException.class, () -> {
      emailVerificationRedisAdapter.getUserId(code);
    });
  }

  @DisplayName("이메일 인증 코드 TTL 만료 후 조회 시 예외 발생")
  @Test
  public void getUserId_shouldThrowException_whenTtlExpired() throws Exception {
    //given
    String userId = "user1";
    String code = "verificationCode123";
    Duration shortTtl = Duration.ofSeconds(2);

    emailVerificationRedisAdapter.saveCode(userId, code, shortTtl);

    //when
    /*TTL 만료 대기*/
    Thread.sleep(2500);

    //then
    assertThrows(EmailVerificationException.class, () -> {
      emailVerificationRedisAdapter.getUserId(code);
    });
  }

  @DisplayName("이메일 인증 코드 삭제 후 조회 시 예외 발생")
  @Test
  public void getUserId_shouldThrowException_whenCodeDeleted() throws Exception {
    //given
    String userId = "user1";
    String code = "verificationCode123";
    Duration duration = Duration.ofSeconds(5);

    emailVerificationRedisAdapter.saveCode(userId, code, duration);

    //when
    emailVerificationRedisAdapter.deleteCode(code);

    //then
    assertThrows(EmailVerificationException.class, () -> {
      emailVerificationRedisAdapter.getUserId(code);
    });
  }
}
