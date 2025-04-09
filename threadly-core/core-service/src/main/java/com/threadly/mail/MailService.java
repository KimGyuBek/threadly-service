package com.threadly.mail;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Email 관련 기능 service
 */
@Service
@RequiredArgsConstructor
public class MailService implements SendMailUseCase {

  private final JavaMailSender mailSender;

  @Override
  public void sendMail(String from, String to, String subject, String body) {

    MimeMessage mimeMessage = mailSender.createMimeMessage();

    try {
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
      helper.setTo(to);
      helper.setFrom("rlarbqor00@naver.com");
      helper.setSubject(subject);
      helper.setText(body, false);
      mailSender.send(mimeMessage);

      System.out.println("Mail sent successfully");

    } catch (Exception e) {
      System.out.println(e.getMessage());
    }


  }
}
