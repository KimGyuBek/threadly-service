package com.threadly.core.service.validator.user;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.user.UserException;
import com.threadly.core.port.user.FetchUserPort;
import com.threadly.core.domain.user.UserStatusType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 사용자 상태 validator
 */
@Component
@RequiredArgsConstructor
public class UserStatusValidator {

  private final FetchUserPort fetchUserPort;

  /**
   * 주어진 userId에 해당하는 사용자의 상태 검증
   * @param userId
   */
  public void validateUserStatusWithException(String userId) {
    UserStatusType userStatusType = fetchUserPort.getUserStatus(userId)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

    switch (userStatusType) {
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

}
