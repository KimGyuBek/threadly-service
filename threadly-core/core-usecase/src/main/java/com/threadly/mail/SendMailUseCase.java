package com.threadly.mail;

/**
 * Email 전송
 */
public interface SendMailUseCase {

  /**
   * mail 전송
   * @param from
   * @param to
   * @param subject
   * @param body
   */
  void sendMail(String from, String to, String subject, String body);




}
