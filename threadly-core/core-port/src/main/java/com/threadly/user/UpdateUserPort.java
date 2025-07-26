package com.threadly.user;

/**
 * user 상태 변경 port
 */
public interface UpdateUserPort {

  /**
   * email verification 변경
   * @param userId
   * @param isEmailVerified
   */
  void updateEmailVerification(String userId, boolean isEmailVerified);

  /**
   * userStatus 변경
   * @param userId
   * @param status
   */
  void updateUserStatus(String userId, UserStatusType status);

}
