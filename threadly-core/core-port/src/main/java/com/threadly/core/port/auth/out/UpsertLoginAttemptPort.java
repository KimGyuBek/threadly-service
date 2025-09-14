package com.threadly.core.port.auth.out;

public interface UpsertLoginAttemptPort {

  /**
   * 로그인 시도 횟수 increase
   * @param insertLoginAttempt
   * @return
   */
  void increaseLoginAttempt(InsertLoginAttempt insertLoginAttempt);


}
