package com.threadly.auth;

import com.threadly.properties.TtlProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 로그인 시도 횟수 제한 service
 */
@Service
@RequiredArgsConstructor
public class LoginAttemptLimiterService {

  private final FetchLoginAttemptPort fetchLoginAttemptPort;
  private final UpsertLoginAttemptPort upsertLoginAttemptPort;
  private final DeleteLoginAttemptPort deleteLoginAttemptPort;

  private final TtlProperties ttlProperties;

  public boolean checkLoginAttempt(String userId) {
    Integer count = fetchLoginAttemptPort.getLoginAttemptCount(userId);
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
    Integer count = fetchLoginAttemptPort.getLoginAttemptCount(userId);
    int loginAttemptCount = (count == null) ? 0 : count;

    if (loginAttemptCount >= 5) {
      return;
    }

    /*값 업데이트*/
    upsertLoginAttemptPort.increaseLoginAttempt(InsertLoginAttempt.builder()
        .userId(userId)
        .loginAttemptCount(loginAttemptCount)
        .duration(ttlProperties.getLoginAttempt())
        .build());
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
    upsertLoginAttemptPort.increaseLoginAttempt(InsertLoginAttempt.builder()
        .userId(userId)
        .loginAttemptCount(loginAttemptCount)
        .duration(ttlProperties.getLoginAttempt())
        .build());
  }

  /**
   * login attempt 삭제
   * @param userId
   */
  public void removeLoginAttempt(String userId) {
    deleteLoginAttemptPort.deleteLoginAttempt(userId);
  }


}
