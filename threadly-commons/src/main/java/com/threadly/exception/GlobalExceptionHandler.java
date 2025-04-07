package com.threadly.exception;

import static com.threadly.ErrorCode.*;

import com.threadly.exception.token.TokenException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  /*TODO error 세분화*/

  /*Token*/
  @ExceptionHandler(TokenException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ErrorResponse handleTokenException(Exception ex, WebRequest request) {
    System.out.println(ex.getMessage());
    System.out.println("globalExceptionHandler");

    return new ErrorResponse(DEFAULT_ERROR);
  }

  /*RuntimeException*/
  @ExceptionHandler(RuntimeException.class)
  public ErrorResponse handleRuntimeException(Exception ex, WebRequest request) {
    System.out.println(ex.getMessage());

    return new ErrorResponse(USER_ALREADY_EXIST);

  }


}
