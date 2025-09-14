package com.threadly.core.port.user.out;

import com.threadly.core.domain.user.User;
import com.threadly.core.domain.user.UserStatusType;
import com.threadly.core.port.user.out.response.UserPortResponse;

/**
 * user 상태 변경 port
 */
public interface UserCommandPort {

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

  UserPortResponse save(User user);
}
