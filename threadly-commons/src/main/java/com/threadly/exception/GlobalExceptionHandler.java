package com.threadly.exception;

import com.threadly.ErrorCode;
import com.threadly.exception.authentication.UserAuthenticationException;
import com.threadly.exception.mail.MailSenderException;
import com.threadly.exception.user.UserException;
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
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  /*Valid*/

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
      HttpHeaders headers, HttpStatusCode status, WebRequest request) {
//    return super.handleMethodArgumentNotValid(ex, headers, status, request);

    return ResponseEntity.status(ex.getStatusCode())
        .body(new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR));
  }

  /*User Authentication*/
  @ExceptionHandler(UserAuthenticationException.class)
  public ResponseEntity<ErrorResponse> handleUserAuthenticationException(
      UserAuthenticationException ex,
      WebRequest request) {
    System.out.println(ex.getMessage());

    return ResponseEntity
        .status(ex.getErrorCode().getHttpStatus())
        .body(new ErrorResponse(ex.getErrorCode()));
  }

  /*User*/
  @ExceptionHandler(UserException.class)
  public ResponseEntity<ErrorResponse> handleUserException(UserException ex, WebRequest request) {

    return ResponseEntity.status(ex.getErrorCode().getHttpStatus())
        .body(new ErrorResponse(ex.getErrorCode()));
  }

  /*Mail*/
  @ExceptionHandler(MailSenderException.class)
  public ResponseEntity<ErrorResponse> handleMailSenderException(MailSenderException ex,
      WebRequest request) {
    return ResponseEntity.status(ex.getErrorCode().getHttpStatus())
        .body(new ErrorResponse(ex.getErrorCode()));
  }
}
