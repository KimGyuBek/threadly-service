package com.threadly.exception;

import static com.threadly.ErrorCode.USER_AUTHENTICATION_FAILED;

import com.threadly.ErrorCode;
import com.threadly.exception.authentication.UserAuthenticationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  /*TODO error 세분화*/

  /*User Authentication*/
  @ExceptionHandler(UserAuthenticationException.class)
  public ResponseEntity<ErrorResponse> handleUserAuthenticationException(UserAuthenticationException ex,
      WebRequest request) {
    System.out.println(ex.getMessage());

    return ResponseEntity
        .status(ex.getErrorCode().getHttpStatus())
        .body(new ErrorResponse(ex.getErrorCode()));
  }

//  /*RuntimeException*/
//  @ExceptionHandler(RuntimeException.class)
//  public ErrorResponse handleRuntimeException(Exception ex, WebRequest request) {
//    System.out.println(ex.getMessage());
//
//    return new ErrorResponse(USER_ALREADY_EXIST);
//  }
}
