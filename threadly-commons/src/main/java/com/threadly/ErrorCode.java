package com.threadly;

import lombok.Getter;

@Getter
public enum ErrorCode {


  /*TODO Error code 세분화*/
  DEFAULT_ERROR("0000", "에러가 발생했습니다."),
  USER_DOES_NOT_EXIST("0001", "사용자가 존재하지 않습니다."),
  USER_ALREADY_EXIST("0002", "사용자가 이미 존재합니다."),

  /*공통*/
  INTERNAL_SERVER_ERROR("TLY0000", "에러가 발생했습니다."),
  INVALID_REQUEST("TLY0001", "잘못된 요청입니다."),
  ACCESS_DENIED("TLY0002", "접근이 거부되었습니다."),

  /*User*/
  USER_NOT_FOUND("TLY2000", "사용자가 존재하지 않습니다."),
  USER_ALREADY_EXISTS("TLY2001", "사용자가 이미 존재합니다."),
  USER_INACTIVE("TLY2002", "비활성화된 사용자입니다."),
  INVALID_PASSWORD("TLY2003", "패스워드가 일치하지 않습니다."),
  ACCOUNT_DISABLED("TLY2004", "비활성화 된 계정입니다."),
  ACCOUNT_LOCKED("TLY2005", "잠긴 계정입니다."),
  AUTHENTICATION_ERROR("TLY2006", "인증 에러"),
  USER_AUTHENTICATION_FAILED("TLY2007", "아이디 또는 비밀번호가 일치하지 않습니다."),

  /*Token*/
  TOKEN_EXPIRED("TLY3000", "토큰이 만료되었습니다."),
  TOKEN_INVALID("TLY3001", "유효하지 않은 토큰입니다."),
  TOKEN_MISSING("TLY3002", "토큰이 존재하지 않습니다.");

  /*TODO 추가*/


  private final String code;
  private final String desc;
//  private final HttpStatus httpStatus;

  ErrorCode(String code, String desc) {
    this.code = code;
    this.desc = desc;
//    this.httpStatus = httpStatus;
  }
}
