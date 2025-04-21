package com.threadly.mail;

/**
 * email 전송
 */
public interface SendMailPort {

  /**
   * 인증 메일 전송
   *
   * @param to
   * @param code
   */
  void sendVerificationMail(String to, String code);

  void sendVerificationCompleteMail(String to, String userName);

}
