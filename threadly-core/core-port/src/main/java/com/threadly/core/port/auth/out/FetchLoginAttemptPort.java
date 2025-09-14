package com.threadly.core.port.auth.out;

/**
 * 로그인 시도 제한 조회 port
 */
public interface FetchLoginAttemptPort  {

  /**
   * redis에서 useId로 조회하여 로그인이 blocked 상태인지 조회
   * @param userId
   * @return
   */
  boolean isLoginBlocked(String userId);

  /**
   * userId로 count 조회
   * @param userId
   * @return
   */
  Integer getLoginAttemptCount(String userId);

}
