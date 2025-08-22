package com.threadly.commons.exception.token;

/**
 * 토큰 없음 : 401 토큰 만료 : 401 서명 이상한 토큰 : 401 권한 부족 : 403
 */

/*TODO exception 세분화 고려*/
public enum TokenErrorType {

  EXPIRED("만료됨"),
  INVALID("유효하지 않음"),
  UNSUPPORTED("존재하지 않음");


  private final String message;

  TokenErrorType(String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    return "토큰 오류 {" + message +"}";
  }
}
