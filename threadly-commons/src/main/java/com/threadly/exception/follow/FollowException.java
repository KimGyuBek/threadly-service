package com.threadly.exception.follow;

import com.threadly.exception.ErrorCode;

/**
 * 팔로우 관련 예외
 */
public class FollowException extends RuntimeException {

  ErrorCode errorCode;

  public FollowException(ErrorCode errorCode) {
    super(errorCode.getDesc());
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
