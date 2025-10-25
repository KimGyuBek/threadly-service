package com.threadly.core.port.auth.out;

public interface LoginAttemptCommandPort {

  /**
   * 로그인 시도 횟수 increase
   * @param insertLoginAttemptCommand
   * @return
   */
  void increaseLoginAttempt(InsertLoginAttemptCommand insertLoginAttemptCommand);

  /**
   * userId에 해당하는 로그인 attempt 삭제
   * @param userId
   */
  void deleteLoginAttempt(String userId);

}
