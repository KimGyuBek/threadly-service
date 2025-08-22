package com.threadly.commons.exception.post;

import com.threadly.commons.exception.ErrorCode;

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
