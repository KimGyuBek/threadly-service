package com.threadly.core.port.user.in.query;

import com.threadly.core.port.user.in.shared.UserResponse;

public interface GetUserUseCase {

  /**
   * Email로 User 조회
   *
   * @param email
   * @return UserResponse
   */
  UserResponse findUserByEmail(String email);

  /**
   * userId로 User 조회
   *
   * @param userId
   * @return
   */
  UserResponse findUserByUserId(String userId);

}
