package com.threadly.exception;

import static com.threadly.ErrorCode.USER_ALREADY_EXIST;
import static com.threadly.ErrorCode.USER_AUTHENTICATION_FAILED;

import com.threadly.ErrorCode;
import com.threadly.exception.authentication.UserAuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  /*TODO error 세분화*/

  /*User Authentication*/
  @ExceptionHandler(UserAuthenticationException.class)
  public ErrorResponse handleUserAuthenticationException(UserAuthenticationException ex,
      WebRequest request) {
    System.out.println(ex.getMessage());

    ErrorCode errorCode = switch (ex.getUserAuthErrorType()) {
      case NOT_FOUND, INVALID_PASSWORD -> USER_AUTHENTICATION_FAILED;
      case ACCOUNT_DISABLED -> ErrorCode.ACCOUNT_DISABLED;
      case ACCOUNT_LOCKED -> ErrorCode.ACCOUNT_LOCKED;
      case AUTHENTICATION_ERROR -> ErrorCode.AUTHENTICATION_ERROR;
    };
    return new ErrorResponse(errorCode);
  }

  /*RuntimeException*/
  @ExceptionHandler(RuntimeException.class)
  public ErrorResponse handleRuntimeException(Exception ex, WebRequest request) {
    System.out.println(ex.getMessage());

    return new ErrorResponse(USER_ALREADY_EXIST);
  }
}
