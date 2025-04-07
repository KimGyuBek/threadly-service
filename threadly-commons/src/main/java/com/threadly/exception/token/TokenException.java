package com.threadly.exception.token;

/**
 * Token 관련 예외
 */
public class TokenException extends RuntimeException {

   TokenErrorType errorType;

  public TokenException(TokenErrorType errorType) {
    super(errorType.toString());
    this.errorType = errorType;
  }

  public TokenErrorType getErrorType() {
    return errorType;
  }
}
