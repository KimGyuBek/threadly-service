package com.threadly.user;

public interface UpdateUserUseCase {

  /**
   * userId로 email 인증
   * @param userId
   */
  void verifyEmail(String userId);

}
