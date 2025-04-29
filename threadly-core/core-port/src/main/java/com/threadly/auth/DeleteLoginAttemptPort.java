package com.threadly.auth;

/**
 * loginAttempt 삭제 port
 */
public interface DeleteLoginAttemptPort {

  /**
   * userId에 해당하는 로그인 attempt 삭제
   * @param userId
   */
  void deleteLoginAttempt(String userId);

}
