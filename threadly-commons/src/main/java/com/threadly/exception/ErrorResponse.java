package com.threadly.exception;

import com.threadly.ErrorCode;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class ErrorResponse {
  private final ErrorCode errorCode;

  public ErrorResponse(ErrorCode errorCode) {
    this.errorCode = errorCode;
  }
}
