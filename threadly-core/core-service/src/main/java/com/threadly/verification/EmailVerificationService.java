package com.threadly.verification;

import com.threadly.mail.SendMailPort;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Email 인증 관련 Service
 */
@Service
@RequiredArgsConstructor
public class EmailVerificationService implements EmailVerificationUseCase {

  private final EmailVerificationPort emailVerificationPort;

  private final SendMailPort sendMailPort;

  /*만료 시간*/
  private static final Duration EXPIRATION = Duration.ofMinutes(5);

  @Override
  public void verificationEmail(String code) {

  }

  @Override
  public void sendVerificationEmail(String userId) {
    /*인증 코드 생성*/
    String code = UUID.randomUUID().toString().substring(0, 6);

    /*인증 코드 저장*/
    emailVerificationPort.saveCode(userId, code, EXPIRATION);

    /*메일 전송*/
    sendMailPort.sendVerificationMail(userId, code);
  }
}
