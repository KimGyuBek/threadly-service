package com.threadly.exception.token;

/**
 * Token 만료 예외
 */
public class TokenException extends RuntimeException {

  private final TokenErrorType errorType;

  public TokenException(TokenErrorType errorType) {
    super(errorType.toString());
    this.errorType = errorType;
  }

  public TokenErrorType getErrorType() {
    return errorType;
  }
}
