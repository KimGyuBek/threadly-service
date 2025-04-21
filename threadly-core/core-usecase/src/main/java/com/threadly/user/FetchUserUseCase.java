package com.threadly.user;

import com.threadly.user.response.UserResponse;

public interface FetchUserUseCase {

  /**
   * Email로 User 조회
   *
   * @param email
   * @return UserResponse
   */
  UserResponse findUserByEmail(String email);

  /**
   * userId로 User 조회
   * @param userId
   * @return
   */
  UserResponse findUserByUserId(String userId);

}
