package com.threadly.user.get;

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

  /**
   * userId에 해당하는 userProfile이 존재하는지 검증
   *
   * @param userId
   * @return
   */
  boolean isUserProfileExists(String userId);

}
