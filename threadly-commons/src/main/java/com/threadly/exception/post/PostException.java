package com.threadly.exception.post;

import com.threadly.exception.ErrorCode;

/**
 * 게시글 관련 예외
 */
public class PostException extends RuntimeException {

  ErrorCode errorCode;

  public PostException(ErrorCode errorCode) {
    super(errorCode.getDesc());
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
