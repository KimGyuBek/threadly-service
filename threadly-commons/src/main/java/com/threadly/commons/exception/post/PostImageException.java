package com.threadly.commons.exception.post;

import com.threadly.commons.exception.ErrorCode;

/**
 * 게시글 이미지 관련 예외
 */
public class PostImageException extends RuntimeException {

  ErrorCode errorCode;

  public PostImageException(ErrorCode errorCode) {
    super(errorCode.getDesc());
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
