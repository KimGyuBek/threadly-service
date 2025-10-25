package com.threadly.auth;

import com.threadly.commons.properties.TtlProperties;
import com.threadly.core.port.auth.out.InsertLoginAttemptCommand;
import com.threadly.core.port.auth.out.LoginAttemptCommandPort;
import com.threadly.core.port.auth.out.LoginAttemptQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 로그인 시도 횟수 제한 처리
 */
@Service
@RequiredArgsConstructor
public class LoginAttemptLimiter {

  private final LoginAttemptQueryPort loginAttemptQueryPort;
  private final LoginAttemptCommandPort loginAttemptCommandPort;

  private final TtlProperties ttlProperties;

  public boolean checkLoginAttempt(String userId) {
    Integer count = loginAttemptQueryPort.getLoginAttemptCount(userId);
    int loginAttemptCount = (count == null) ? 0 : count;

    if (loginAttemptCount >= 5) {
      return false;
    }
    return true;
  }

  /**
   * login Attempt count 체크 로그인 가능하면 true 아니면 false
   *
   * @param userId
   * @return
   */
  public void upsertLoginAttempt(String userId) {
    Integer count = loginAttemptQueryPort.getLoginAttemptCount(userId);
    int loginAttemptCount = (count == null) ? 0 : count;

    if (loginAttemptCount >= 5) {
      return;
    }

    /*값 업데이트*/
    loginAttemptCommandPort.increaseLoginAttempt(
        new InsertLoginAttemptCommand(
            userId,
            loginAttemptCount,
            ttlProperties.getLoginAttempt()
        ));
  }

  /**
   * increase LoginAttempt
   *
   * @param userId
   */
  public void incrementLoginAttempt(String userId, int loginAttemptCount) {
    /*시도횟수 이상인 경우*/
    if (loginAttemptCount >= 5) {
      return;
    }

    /*값 업데이트*/
    loginAttemptCommandPort.increaseLoginAttempt(new InsertLoginAttemptCommand(
        userId,
        loginAttemptCount,
        ttlProperties.getLoginAttempt()
    ));
  }

  /**
   * login attempt 삭제
   *
   * @param userId
   */
  public void removeLoginAttempt(String userId) {
    loginAttemptCommandPort.deleteLoginAttempt(userId);
  }


}
