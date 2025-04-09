package com.threadly.user;

public interface UpdateUserUseCase {

  /**
   * email 인증
   * @param code
   * @return
   */
  boolean validateEmail(String code);

}
