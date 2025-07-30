package com.threadly.exception.user;

import com.threadly.exception.ErrorCode;

/**
 * 사용자 프로필 이미지 관련 예외
 */
public class UserProfileImageException extends RuntimeException {

  ErrorCode errorCode;

  public UserProfileImageException(ErrorCode errorCode) {
    super(errorCode.getDesc());
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
