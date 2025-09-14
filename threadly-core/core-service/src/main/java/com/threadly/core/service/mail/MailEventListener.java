package com.threadly.core.service.mail;

import com.threadly.core.port.mail.in.SendMailCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Mail 이벤트 listener
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MailEventListener {

  private final MailService mailService;

  /**
   * 메일 발행 이벤트 감지
   *
   * @param command
   */
  @Async("eventExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onSendMail(SendMailCommand command) {
    try {
      switch (command.mailType()) {
        case VERIFICATION -> {
          mailService.sendVerificationMail(command);
          log.debug("인증 메일 전송 userId={}, email={}", command.userId(), command.email());
        }
        case WELCOME -> {
          mailService.sendWelcomeMail(command);
          log.debug("가입 환영 메일 전송 userId={}, email={}", command.userId(), command.email());
        }
      }

    } catch (Exception e) {
      log.error("메일 전송 실패: {}", e.getMessage());
    }
  }

}
