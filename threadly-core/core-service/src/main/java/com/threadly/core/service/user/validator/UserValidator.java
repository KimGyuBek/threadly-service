package com.threadly.core.service.user.validator;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.user.UserException;
import com.threadly.core.domain.user.User;
import com.threadly.core.domain.user.UserStatus;
import com.threadly.core.port.user.out.UserQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 사용자 상태 validator
 */
@Component
@RequiredArgsConstructor
@Slf4j
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

    log.debug("userStatus: {}", userStatus);

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
   * 주어진 userStatus 검증
   *
   * @param userStatus
   * @throws UserException
   */
  public void validateUserStatusWithException(UserStatus userStatus) {
    log.debug("userStatus: {}", userStatus);

    if (userStatus.equals(UserStatus.DELETED)) {
      throw new UserException(ErrorCode.USER_ALREADY_DELETED);
    } else if (userStatus.equals(UserStatus.INACTIVE)) {
      throw new UserException(ErrorCode.USER_INACTIVE);
    }
  }

  /**
   * 주어진 userStatus을 통계 내 계정 상태 검증
   *
   * @param userStatus
   * @throws UserException
   */
  public void validateMyStatusWithException(UserStatus userStatus) {
    log.debug("userStatus: {}", userStatus);

    if (userStatus.equals(UserStatus.DELETED)) {
      throw new UserException(ErrorCode.USER_ALREADY_DELETED);
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

  /**
   * 주어진 email의 중복 검증
   *
   * @param email
   * @throws UserException
   */
  public void validateEmailDuplicate(String email) {
    if (userQueryPort.existsByEmail(email)) {
      log.info("이미 존재하는 이메일: {}", email);

      throw new UserException(ErrorCode.USER_ALREADY_EXISTS);
    }
  }


}
