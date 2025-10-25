package com.threadly.core.port.auth.in.verification;

public interface EmailVerificationUseCase {

  /**
   * 인증 메일 검증
   *
   * @param code
   */
  void verifyEmail(String code);

}
