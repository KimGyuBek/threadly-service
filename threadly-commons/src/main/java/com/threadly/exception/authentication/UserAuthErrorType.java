package com.threadly.exception.authentication;

import org.springframework.http.HttpStatus;


/*TODO exception 세분화 고려*/
public enum UserAuthErrorType {

  NOT_FOUND("사용자를 찾을 수 없음", HttpStatus.NOT_FOUND),
  INVALID_PASSWORD("패스워드 오류", HttpStatus.UNAUTHORIZED),
  ACCOUNT_DISABLED("계정이 비활성화 됨", HttpStatus.FORBIDDEN),
  ACCOUNT_LOCKED("계정이 잠김", HttpStatus.LOCKED),
  AUTHENTICATION_ERROR("인증 오류", HttpStatus.UNAUTHORIZED);


  private final String message;
  private final HttpStatus status;

  UserAuthErrorType(String message, HttpStatus status) {
      this.message = message;
      this.status = status;
  }

  public HttpStatus getStatus() {
      return status;
  }

  @Override
  public String toString() {
    return "사용자 인증 오류 {" + message +"}";
  }
}
