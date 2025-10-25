package com.threadly.commons.exception.search;

import com.threadly.commons.exception.ErrorCode;

/**
 * 검색 관련 예외
 */
public class SearchException extends RuntimeException {

  ErrorCode errorCode;

  public SearchException(ErrorCode errorCode) {
    super(errorCode.getDesc());
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
