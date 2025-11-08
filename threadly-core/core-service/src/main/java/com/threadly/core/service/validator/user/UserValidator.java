package com.threadly.core.service.validator.user;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.user.UserException;
import com.threadly.core.domain.user.User;
import com.threadly.core.domain.user.UserStatus;
import com.threadly.core.port.user.out.UserQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 사용자 상태 validator
 */
@Component
@RequiredArgsConstructor
public class UserValidator {

  private final UserQueryPort userQueryPort;

  /**
   * 주어진 userId에 해당하는 사용자의 상태 검증
   *
   * @param userId
   */
  public void validateUserStatusWithException(String userId) {
    UserStatus userStatus = userQueryPort.getUserStatus(userId)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

    switch (userStatus) {
      case ACTIVE:
        break;
      case DELETED:
        throw new UserException(ErrorCode.USER_ALREADY_DELETED);
      case INCOMPLETE_PROFILE:
        throw new UserException(ErrorCode.USER_PROFILE_NOT_SET);
      case INACTIVE:
        throw new UserException(ErrorCode.USER_INACTIVE);
    }
  }

  /**
   * 주어진 email에 해당하는 사용자 조회
   *
   * @param email
   * @return
   * @throws UserException
   */
  public User getUserByEmailOrThrow(String email) {
    return userQueryPort.findByEmail(email)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
  }

  /**
   * 주어진 userId에 해당하는 사용자 조회
   *
   * @param userId
   * @return
   * @throws UserException
   */
  public User getUserByIdOrElseThrow(String userId) {
    return userQueryPort.findByUserId(userId)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
  }

}
