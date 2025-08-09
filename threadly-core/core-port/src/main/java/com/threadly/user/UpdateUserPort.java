package com.threadly.user;

/**
 * user 상태 변경 port
 */
public interface UpdateUserPort {

  /**
   * email verification 변경
   *
   * @param userId
   * @param isEmailVerified
   */
  void updateEmailVerification(String userId, boolean isEmailVerified);

  /**
   * userStatus 변경
   *
   * @param userId
   * @param status
   */
  void updateUserStatus(String userId, UserStatusType status);

  /**
   * 주어진 userId에 해당하는 phone을 업데이트
   *
   * @param userId
   * @param phone
   */
  void updateUserPhone(String userId, String phone);

  /**
   * 주어진 userId에 해당하는 사용자의 비밀번호 변경
   *
   * @param userId
   * @param newPassword
   */
  void changePassword(String userId, String newPassword);

  /**
   * 주어진 user 도메인에 해당하는 사용자의 isPrivate 변경
   *
   * @param user
   */
  void updatePrivacy(User user);
}
