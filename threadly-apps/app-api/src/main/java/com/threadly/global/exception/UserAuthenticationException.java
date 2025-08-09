package com.threadly.global.exception;

import com.threadly.commons.exception.ErrorCode;
import org.springframework.security.core.AuthenticationException;

/**
 * 사용자 인증 관련 예외
 */
public class UserAuthenticationException extends AuthenticationException {

  ErrorCode errorCode;

  public UserAuthenticationException(ErrorCode errorCode) {
    super(errorCode.getDesc());
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
