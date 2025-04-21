package com.threadly.auth.verification.response;

import lombok.Getter;

/**
 * 사용자 이중 인증을 위한 코드
 */
@Getter
public class PasswordVerificationToken {

  private String verifyToken;

  public PasswordVerificationToken(String verifyToken) {
    this.verifyToken = verifyToken;
  }
}
