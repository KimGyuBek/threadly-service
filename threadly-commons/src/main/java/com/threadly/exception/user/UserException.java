package com.threadly.exception.user;

import com.threadly.ErrorCode;

/**
 * 사용자 관련 예외
 */
public class UserException extends RuntimeException {

  ErrorCode errorCode;

  public UserException(ErrorCode errorCode) {
    super(errorCode.getDesc());
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
