package com.threadly.exception.authentication;

/**
 * 사용자 인증 관련 예외
 */
public class UserAuthenticationException extends RuntimeException{

    UserAuthErrorType userAuthErrorType;

  public UserAuthenticationException(UserAuthErrorType userAuthErrorType) {
    super(userAuthErrorType.toString());
    this.userAuthErrorType = userAuthErrorType;
  }

  public UserAuthErrorType getUserAuthErrorType() {
    return userAuthErrorType;
  }
}
