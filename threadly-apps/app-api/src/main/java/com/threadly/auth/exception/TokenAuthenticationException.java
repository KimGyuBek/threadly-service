package com.threadly.auth.exception;

import com.threadly.ErrorCode;
import org.springframework.security.core.AuthenticationException;

/**
 * Token 인증 관련 예외
 */
public class TokenAuthenticationException extends AuthenticationException {

  ErrorCode errorCode;

  public TokenAuthenticationException(ErrorCode errorCode) {
    super(errorCode.getDesc());
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
