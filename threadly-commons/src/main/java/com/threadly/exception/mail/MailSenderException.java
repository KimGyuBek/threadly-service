package com.threadly.exception.mail;

import com.threadly.ErrorCode;

/**
 * 메일 전송 관련 예외
 */
public class MailSenderException extends RuntimeException {

  private  ErrorCode errorCode;

  public MailSenderException(ErrorCode errorCode) {
    super(errorCode.getDesc());
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
