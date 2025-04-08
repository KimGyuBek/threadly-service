package com.threadly;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {


  /*공통*/
  INTERNAL_SERVER_ERROR("TLY0000", "에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  INVALID_REQUEST("TLY0001", "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
  ACCESS_DENIED("TLY0002", "접근이 거부되었습니다.", HttpStatus.FORBIDDEN),

  /*User*/
  USER_NOT_FOUND("TLY2000", "사용자가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
  USER_ALREADY_EXISTS("TLY2001", "사용자가 이미 존재합니다.", HttpStatus.CONFLICT),
  USER_INACTIVE("TLY2002", "비활성화된 사용자입니다.", HttpStatus.FORBIDDEN),
  INVALID_PASSWORD("TLY2003", "패스워드가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
  ACCOUNT_DISABLED("TLY2004", "비활성화 된 계정입니다.", HttpStatus.FORBIDDEN),
  ACCOUNT_LOCKED("TLY2005", "잠긴 계정입니다.", HttpStatus.LOCKED),
  AUTHENTICATION_ERROR("TLY2006", "인증 에러", HttpStatus.UNAUTHORIZED),
  USER_AUTHENTICATION_FAILED("TLY2007", "아이디 또는 비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
  DUPLICATE_USER_NAME("TLY2008", "이미 사용 중인 사용자 이름입니다.", HttpStatus.CONFLICT),
  DUPLICATE_EMAIL("TLY2009", "이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT),
  PASSWORD_REQUIRED("TLY2010", "패스워드는 필수입니다.", HttpStatus.BAD_REQUEST),
  INVALID_USER_STATUS("TLY2011", "유효하지 않은 사용자 상태입니다.", HttpStatus.BAD_REQUEST),
  USER_ALREADY_DELETED("TLY2012", "이미 삭제된 사용자입니다.", HttpStatus.BAD_REQUEST),

  /*Token*/
  TOKEN_EXPIRED("TLY3000", "토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
  TOKEN_INVALID("TLY3001", "유효하지 않은 토큰입니다.", HttpStatus.BAD_REQUEST),
  TOKEN_MISSING("TLY3002", "토큰이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);

  private final String code;
  private final String desc;
  private final HttpStatus httpStatus;

  ErrorCode(String code, String desc, HttpStatus httpStatus) {
    this.code = code;
    this.desc = desc;
    this.httpStatus = httpStatus;
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }
}
