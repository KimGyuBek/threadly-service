package com.threadly.exception.authentication;

import com.threadly.ErrorCode;

/**
 * 사용자 인증 관련 예외
 */
public class UserAuthenticationException extends RuntimeException{

  ErrorCode errorCode;

  public UserAuthenticationException(ErrorCode errorCode) {
    super(errorCode.getDesc());
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
