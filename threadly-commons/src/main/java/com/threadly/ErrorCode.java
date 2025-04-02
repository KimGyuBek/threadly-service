package com.threadly;

import static org.springframework.http.HttpStatus.*;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

  DEFAULT_ERROR("0000", "에러가 발생했습니다.", INTERNAL_SERVER_ERROR),
  USER_DOES_NOT_EXIST("0001", "사용자가 존재하지 않습니다.", NOT_FOUND),
  USER_ALREADY_EXIST("0002", "사용자가 이미 존재합니다.", CONFLICT),;

  /*TODO*/

  private final String code;
  private final String desc;
  private final HttpStatus httpStatus;

  ErrorCode(String code, String desc, HttpStatus httpStatus) {
    this.code = code;
    this.desc = desc;
    this.httpStatus = httpStatus;
  }
}
