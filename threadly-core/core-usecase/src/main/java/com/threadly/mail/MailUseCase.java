package com.threadly.mail;

public interface MailUseCase {

  /**
   * 인증 메일 전송
   * @param userId
   * @param email
   */
  void sendVerificationEmail(String userId, String email);


}
