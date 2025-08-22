package com.threadly.commons.exception;

import com.threadly.commons.exception.follow.FollowException;
import com.threadly.commons.exception.mail.EmailVerificationException;
import com.threadly.commons.exception.post.PostCommentException;
import com.threadly.commons.exception.post.PostException;
import com.threadly.commons.exception.post.PostImageException;
import com.threadly.commons.exception.token.TokenException;
import com.threadly.commons.exception.user.UserException;
import com.threadly.commons.exception.user.UserProfileImageException;
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

    System.out.println(ex.getBindingResult().getAllErrors().toString());

    return ResponseEntity.status(ErrorCode.INVALID_REQUEST.getHttpStatus())
        .body(new ErrorResponse(ErrorCode.INVALID_REQUEST));
  }

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

  /*Post Exception*/
  @ExceptionHandler(PostException.class)
  public ResponseEntity<ErrorResponse> handlePostException(PostException ex, WebRequest request) {
    return ResponseEntity.status(ex.getErrorCode().getHttpStatus())
        .body(new ErrorResponse(ex.getErrorCode()));
  }

  /*Post Image Exception*/
  @ExceptionHandler(PostImageException.class)
  public ResponseEntity<ErrorResponse> handlePostImageException(PostImageException ex,
      WebRequest request) {
    return ResponseEntity.status(ex.getErrorCode().getHttpStatus())
        .body(new ErrorResponse(ex.getErrorCode()));
  }

  /*Post Comment Exception*/
  @ExceptionHandler(PostCommentException.class)
  public ResponseEntity<ErrorResponse> handlePostCommentException(PostCommentException ex,
      WebRequest request) {
    return ResponseEntity.status(ex.getErrorCode().getHttpStatus())
        .body(new ErrorResponse(ex.getErrorCode()));
  }

  /*User Profile Image Exception*/
  @ExceptionHandler(UserProfileImageException.class)
  public ResponseEntity<ErrorResponse> handleUserProfileImageException(UserProfileImageException ex,
      WebRequest request) {
    return ResponseEntity.status(ex.getErrorCode().getHttpStatus())
        .body(new ErrorResponse(ex.getErrorCode()));
  }

  /*Follow Exception*/
  @ExceptionHandler(FollowException.class)
  public ResponseEntity<ErrorResponse> handleFollowException(FollowException ex,
      WebRequest request) {
    return ResponseEntity.status(ex.getErrorCode().getHttpStatus())
        .body(new ErrorResponse(ex.getErrorCode()));
  }
}
