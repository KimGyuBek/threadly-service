package com.threadly.exception;

import com.threadly.ErrorCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(RuntimeException.class)
  public ErrorResponse handleRuntimeException(Exception ex, WebRequest request) {
    System.out.println(ex.getMessage());

    return new ErrorResponse(ErrorCode.USER_ALREADY_EXIST);

  }


}
