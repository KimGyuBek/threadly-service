package com.threadly.core.port.mail;

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

  /**
   * 환영 메일 전송
   *
   * @param to
   * @param userName
   */
  void sendWelcomeMail(String to, String userName);

}
