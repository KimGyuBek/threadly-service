package com.threadly.core.service.mail;

import com.threadly.commons.properties.TtlProperties;
import com.threadly.commons.utils.RandomUtils;
import com.threadly.core.domain.mail.MailType;
import com.threadly.core.domain.mail.model.VerificationModel;
import com.threadly.core.domain.mail.model.WelcomeModel;
import com.threadly.core.port.mail.out.MailEventPublisherPort;
import com.threadly.core.port.verification.EmailVerificationPort;
import com.threadly.core.port.mail.in.SendMailCommand;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 메일 발신 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

  private final MailEventPublisherPort mailEventPublisherPort;
  private final EmailVerificationPort emailVerificationPort;

  private final TtlProperties ttlProperties;

  @Value("${properties.email.verification-url}")
  private String baseUrl;

  /**
   * 가입 환영 메일 전송
   */
  public void sendWelcomeMail(SendMailCommand sendMailCommand) {
    /*로그인 url 생성*/
    String loginUrl = baseUrl + "/api/auth/login";

    /*Kafka 이벤트 발행*/
    mailEventPublisherPort.publish(
        RandomUtils.generateNanoId(),
        MailType.WELCOME,
        sendMailCommand.email(),
        new WelcomeModel(
            sendMailCommand.userName(),
            loginUrl
        )
    );
  }


  /**
   * 인증 메일 전송
   *
   * @param command
   */
  public void sendVerificationMail(SendMailCommand command) {
    /*인증 코드 생성*/
    String code = UUID.randomUUID().toString().substring(0, 6);

    log.debug("인증 코드 : {}", code);

    /*인증 코드 저장*/
    emailVerificationPort.saveCode(command.userId(), code, ttlProperties.getAccessToken());

    /*인증 url 생성*/
    String verificationUrl = baseUrl + "/api/auth/verify-email?code=" + code;

    log.debug("verificationUrl: {}", verificationUrl);

    /*kafka 이벤트 발행*/
    mailEventPublisherPort.publish(
        RandomUtils.generateNanoId(),
        MailType.VERIFICATION,
        command.email(),
        new VerificationModel(
            command.userName(),
            verificationUrl
        )
    );
  }


}
