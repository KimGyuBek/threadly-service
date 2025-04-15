package com.threadly.client;

import com.threadly.ErrorCode;
import com.threadly.exception.mail.MailSenderException;
import com.threadly.mail.SendMailPort;
import jakarta.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
@RequiredArgsConstructor
public class MailClient implements SendMailPort {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  private final static String FROM = "rlarbqor00@naver.com";

  @Override
  public void sendVerificationMail(String to, String code) {

    MimeMessage mimeMessage = mailSender.createMimeMessage();

    Map<String, Object> values = new HashMap<>();
    values.put("code", code);

    String subject = "[Threadly] 본인 인증을 위한 메일입니다.";

    String context = getContext(values);

    try {
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
      helper.setFrom(FROM);
      helper.setTo(FROM);
      helper.setSubject(subject);
      helper.setText(context, true);

      mailSender.send(mimeMessage);

      System.out.println("인증 메일 전송 완료 : " + to);

    } catch (Exception e) {
      throw new MailSenderException(ErrorCode.SEND_MAIL_ERROR);
    }


  }

  private String getContext(Map<String, Object> values) {
    Context context = new Context();
    context.setVariables(values);

    return templateEngine.process("verify-email", context);
  }

}
