package com.threadly.verification;

public interface EmailVerificationUseCase {

  /**
   * code로 이메일 인증
   * @param code
   */
  void verificationEmail(String code);

  /**
   * 인증 메일 전송
   * @param userId
   */
  void sendVerificationEmail(String userId);

}
