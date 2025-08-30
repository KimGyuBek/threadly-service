package com.threadly.commons.response;

import com.threadly.commons.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ErrorResponse {
  private final ErrorCode errorCode;

  public ErrorResponse(ErrorCode errorCode) {
    this.errorCode = errorCode;
  }
}
