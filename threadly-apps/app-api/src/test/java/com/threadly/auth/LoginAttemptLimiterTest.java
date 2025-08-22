package com.threadly.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.threadly.adapter.redis.repository.auth.TestRedisHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

/**
 * LoginAttemptLimiterService Test
 */

@ActiveProfiles("test")
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class LoginAttemptLimiterTest {

  @Autowired
  private LoginAttemptLimiter loginAttemptLimiter;

  @Autowired
  private TestRedisHelper testLoginAttemptHelper;

  private static final int MAX_LOGIN_ATTEMPTS = 5;

  @BeforeEach
  public void setUp() {
    testLoginAttemptHelper.clearRedis();
  }


  /*
   * checkLoginAttempt()
   */
  /*[Case #1] userId에 일치하는 loginAttempt가 존재하지 않을 경우 true를 리턴해야한다*/
  @DisplayName("loginAttempt가 존재하지 않을 경우 - true 리턴")
  @Test
  public void upsertLoginAttempt_shouldReturnTrue_whenLoginAttemptLessThanFive() throws Exception {
    //given
    String userId = "userId";

    //when
     loginAttemptLimiter.upsertLoginAttempt(userId);
    boolean result = loginAttemptLimiter.checkLoginAttempt(userId);

    //then
    assertTrue(result);
  }

  /*[Case #2] userId에 일치하는 loginAttempt가 5이상일 경우 false를 리턴해야한다*/
  @DisplayName("loginAttempt가 5이상일 경우 - false 리턴")
  @Test
  public void upsertLoginAttempt_shouldReturnTrue_whenLoginAttemptReachThanFive() throws Exception {
    //given
    String userId = "userId";

    //when
    testLoginAttemptHelper.insertLoginAttempt(userId, MAX_LOGIN_ATTEMPTS);

    boolean result = loginAttemptLimiter.checkLoginAttempt(userId);

    //then
    assertFalse(result);
  }

  /*[Case #3] userId에 해당하는 값이 존재하지 않는 상황에서 3번 호출된 후 조회 시 3이 나와야 한다*/
  @DisplayName("userId에 해당하는 값이 존재하지 않는 상황에서 3번 호출된 후 조회 시 3이 나와야 한다")
  @Test
  public void upsertLoginAttempt_shouldReturnThree_whenLoginAttemptReachCallThree()
      throws Exception {
    //given
    String userId = "userId";

    //when
    for (int i = 0; i < 3; i++) {
      loginAttemptLimiter.upsertLoginAttempt(userId);
    }

    loginAttemptLimiter.upsertLoginAttempt(userId);
    boolean result = loginAttemptLimiter.checkLoginAttempt(userId);

    Integer loginAttemptCount = testLoginAttemptHelper.getLoginAttemptCount(userId);

    //then
    assertTrue(result);
    assertThat(loginAttemptCount - 1).isEqualTo(3);
  }

  /*[Case #4]  6번 호출된 후 조회 시 5가 나와야 하고 false가 return 되어야 한다*/
  @DisplayName("6번 호출된 후 조회 시 5가 나와야 하고 false가 return 되어야 한다")
  @Test
  public void upsertLoginAttempt_shouldReturnFive_andFalse_whenLoginAttemptReachCallSix()
      throws Exception {
    //given
    String userId = "userId";

    //when
    for (int i = 0; i < MAX_LOGIN_ATTEMPTS + 1; i++) {
      loginAttemptLimiter.upsertLoginAttempt(userId);
    }

    loginAttemptLimiter.upsertLoginAttempt(userId);
    boolean result = loginAttemptLimiter.checkLoginAttempt(userId);

    Integer loginAttemptCount = testLoginAttemptHelper.getLoginAttemptCount(userId);

    //then
    assertFalse(result);
    assertThat(loginAttemptCount).isEqualTo(MAX_LOGIN_ATTEMPTS);
  }

  /*
   * incrementLoginAttempt()
   */
  /*[Case #1] loginAttempt가 존재하지 않는 경우, 실행시 1이 더해져야 함*/
  @DisplayName("loginAttempt가 존재하지 않는 경우, 실행시 1이 더해져야 함")
  @Test
  public void incrementLoginAttempt_shouldReturnOne_whenLoginAttemptNotExists() throws Exception {
    //given
    String userId = "userId";

    //when
    loginAttemptLimiter.incrementLoginAttempt(userId, 0);

    Integer result = testLoginAttemptHelper.getLoginAttemptCount(userId);

    //then
    assertAll(
        () -> assertNotNull(result),
        () -> assertThat(result).isEqualTo(1)
    );
  }

  /*[Case #2] loginAttempt가 5인 경우, 더 이상 업데이트 되지 않고 5로 유지되어야 함*/
  @DisplayName("loginAttempt가 5인 경우, 더 이상 업데이트 되지 않고 5로 유지되어야 함")
  @Test
  public void incrementLoginAttempt_shouldReturnFive_whenLoginAttemptIsFive() throws Exception {
    //given
    String userId = "userId";
    testLoginAttemptHelper.insertLoginAttempt(userId, MAX_LOGIN_ATTEMPTS);

    //when
    loginAttemptLimiter.incrementLoginAttempt(userId, MAX_LOGIN_ATTEMPTS);

    Integer result = testLoginAttemptHelper.getLoginAttemptCount(userId);

    //then
    assertAll(
        () -> assertNotNull(result),
        () -> assertThat(result).isEqualTo(5)
    );
  }

  /*
   * removeLoginAttempt()
   */
  /*[Case #1] login attempt를 삽입한 후 실행, 이후 조회시 null이 나와야한다*/
  @DisplayName("login attempt를 삽입한 후 실행, 이후 조회시 null이 나와야한다")
  @Test
  public void deleteLoginAttempt_shouldReturnNull() throws Exception {
    //given
    String userId = "userId";
    String key = "login:attempt:" + userId;

    testLoginAttemptHelper.insertLoginAttempt(userId, 3);

    //when
    loginAttemptLimiter.removeLoginAttempt(userId);

    Integer result = testLoginAttemptHelper.getLoginAttemptCount(userId);

    //then
    assertNull(result);

  }
}