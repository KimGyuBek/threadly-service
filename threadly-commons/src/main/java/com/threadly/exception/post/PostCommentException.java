package com.threadly.exception.post;

import com.threadly.exception.ErrorCode;

/**
 * 게시글 댓글 관련 예외
 */
public class PostCommentException extends RuntimeException {

  ErrorCode errorCode;

  public PostCommentException(ErrorCode errorCode) {
    super(errorCode.getDesc());
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
