package com.threadly.core.usecase.auth.verification;

public interface EmailVerificationUseCase {

  /**
   * 인증 메일 검증
   *
   * @param code
   */
  void verifyEmail(String code);

}
