package com.threadly.core.port.auth.out;

/**
 * 로그인 시도 제한 query port
 */
public interface LoginAttemptQueryPort {

  /**
   * userId로 count 조회
   * @param userId
   * @return
   */
  Integer getLoginAttemptCount(String userId);

}
