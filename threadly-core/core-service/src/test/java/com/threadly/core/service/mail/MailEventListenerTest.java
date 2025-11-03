package com.threadly.core.service.mail;

import static org.mockito.Mockito.verify;

import com.threadly.core.domain.mail.MailType;
import com.threadly.core.port.mail.in.SendMailCommand;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * MailEventListener 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class MailEventListenerTest {

  @InjectMocks
  private MailEventListener mailEventListener;

  @Mock
  private MailService mailService;

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    /*[Case #1] 인증 메일 이벤트 처리*/
    @Order(1)
    @DisplayName("1. 인증 메일 이벤트를 수신하면 인증 메일이 전송되는지 검증")
    @Test
    void onSendMail_shouldDispatchVerificationMail() throws Exception {
      //given
      SendMailCommand command = new SendMailCommand(
          "user-1",
          "test@threadly.io",
          "사용자",
          MailType.VERIFICATION
      );

      //when
      mailEventListener.onSendMail(command);

      //then
      verify(mailService).sendVerificationMail(command);
    }

    /*[Case #2] 환영 메일 이벤트 처리*/
    @Order(2)
    @DisplayName("2. 환영 메일 이벤트를 수신하면 환영 메일이 전송되는지 검증")
    @Test
    void onSendMail_shouldDispatchWelcomeMail() throws Exception {
      //given
      SendMailCommand command = new SendMailCommand(
          "user-1",
          "test@threadly.io",
          "사용자",
          MailType.WELCOME
      );

      //when
      mailEventListener.onSendMail(command);

      //then
      verify(mailService).sendWelcomeMail(command);
    }
  }
}
