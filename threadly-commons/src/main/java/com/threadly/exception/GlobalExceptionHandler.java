package com.threadly.exception;

import com.threadly.ErrorCode;
import com.threadly.exception.mail.EmailVerificationException;
import com.threadly.exception.token.TokenException;
import com.threadly.exception.user.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Global Exception Handler
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  /*Valid*/

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
      HttpHeaders headers, HttpStatusCode status, WebRequest request) {
//    return super.handleMethodArgumentNotValid(ex, headers, status, request);

    return ResponseEntity.status(ex.getStatusCode())
        .body(new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR));
  }

//  /*User Authentication*/
//  @ExceptionHandler(UserException.class)
//  public ResponseEntity<ErrorResponse> handleUserAuthenticationException(
//      UserException ex,
//      WebRequest request) {
//
//    return ResponseEntity
//        .status(ex.getErrorCode().getHttpStatus())
//        .body(new ErrorResponse(ex.getErrorCode()));
//  }

  /*User*/
  @ExceptionHandler(UserException.class)
  public ResponseEntity<ErrorResponse> handleUserException(UserException ex, WebRequest request) {

    return ResponseEntity.status(ex.getErrorCode().getHttpStatus())
        .body(new ErrorResponse(ex.getErrorCode()));
  }

  /*Email Verification*/
  @ExceptionHandler(EmailVerificationException.class)
  public ResponseEntity<ErrorResponse> handleMailSenderException(EmailVerificationException ex,
      WebRequest request) {
    return ResponseEntity.status(ex.getErrorCode().getHttpStatus())
        .body(new ErrorResponse(ex.getErrorCode()));
  }

  /*Token Exception*/
  @ExceptionHandler(TokenException.class)
  public ResponseEntity<ErrorResponse> handleTokenException(TokenException ex, WebRequest request) {
    return ResponseEntity.status(ex.getErrorCode().getHttpStatus())
        .body(new ErrorResponse(ex.getErrorCode()));
  }

}
