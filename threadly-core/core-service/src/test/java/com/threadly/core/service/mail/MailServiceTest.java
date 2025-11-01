package com.threadly.core.service.mail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.commons.properties.TtlProperties;
import com.threadly.core.domain.mail.MailType;
import com.threadly.core.domain.mail.model.MailModel;
import com.threadly.core.domain.mail.model.VerificationModel;
import com.threadly.core.domain.mail.model.WelcomeModel;
import com.threadly.core.port.mail.in.SendMailCommand;
import com.threadly.core.port.mail.out.MailEventPublisherPort;
import com.threadly.core.port.verification.EmailVerificationPort;
import java.time.Duration;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * MailService 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class MailServiceTest {

  @InjectMocks
  private MailService mailService;

  @Mock
  private MailEventPublisherPort mailEventPublisherPort;

  @Mock
  private EmailVerificationPort emailVerificationPort;

  @Mock
  private TtlProperties ttlProperties;

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("가입 환영 메일 전송")
  class SendWelcomeMailTest {

    /*[Case #1] 가입 환영 메일 발행*/
    @Order(1)
    @DisplayName("1. 가입 환영 메일 이벤트가 발행되는지 검증")
    @Test
    void sendWelcomeMail_shouldPublishWelcomeEvent() throws Exception {
      //given
      ReflectionTestUtils.setField(mailService, "baseUrl", "https://threadly.io");

      SendMailCommand command = new SendMailCommand(
          "user-1",
          "test@threadly.io",
          "사용자",
          MailType.WELCOME
      );

      ArgumentCaptor<String> eventIdCaptor = ArgumentCaptor.forClass(String.class);
      ArgumentCaptor<MailType> typeCaptor = ArgumentCaptor.forClass(MailType.class);
      ArgumentCaptor<String> toCaptor = ArgumentCaptor.forClass(String.class);
      ArgumentCaptor<MailModel> modelCaptor = ArgumentCaptor.forClass(MailModel.class);

      //when
      mailService.sendWelcomeMail(command);

      //then
      verify(mailEventPublisherPort).publish(
          eventIdCaptor.capture(),
          typeCaptor.capture(),
          toCaptor.capture(),
          modelCaptor.capture()
      );

      assertThat(eventIdCaptor.getValue()).isNotBlank();
      assertThat(typeCaptor.getValue()).isEqualTo(MailType.WELCOME);
      assertThat(toCaptor.getValue()).isEqualTo(command.email());

      WelcomeModel model = (WelcomeModel) modelCaptor.getValue();
      assertThat(model.userName()).isEqualTo(command.userName());
      assertThat(model.loginUrl()).isEqualTo("https://threadly.io/api/auth/login");
    }
  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("이메일 인증 메일 전송")
  class SendVerificationMailTest {

    /*[Case #1] 이메일 인증 메일 발행*/
    @Order(1)
    @DisplayName("1. 인증 메일 발행 시 코드가 저장되고 이벤트가 발행되는지 검증")
    @Test
    void sendVerificationMail_shouldSaveCodeAndPublishEvent() throws Exception {
      //given
      ReflectionTestUtils.setField(mailService, "baseUrl", "https://threadly.io");
      Duration expectedDuration = Duration.ofMinutes(5);
      when(ttlProperties.getAccessToken()).thenReturn(expectedDuration);

      SendMailCommand command = new SendMailCommand(
          "user-1",
          "test@threadly.io",
          "사용자",
          MailType.VERIFICATION
      );

      ArgumentCaptor<String> codeCaptor = ArgumentCaptor.forClass(String.class);
      ArgumentCaptor<Duration> durationCaptor = ArgumentCaptor.forClass(Duration.class);
      ArgumentCaptor<String> eventIdCaptor = ArgumentCaptor.forClass(String.class);
      ArgumentCaptor<MailType> typeCaptor = ArgumentCaptor.forClass(MailType.class);
      ArgumentCaptor<String> toCaptor = ArgumentCaptor.forClass(String.class);
      ArgumentCaptor<MailModel> modelCaptor = ArgumentCaptor.forClass(MailModel.class);

      //when
      mailService.sendVerificationMail(command);

      //then
      verify(emailVerificationPort).saveCode(eq(command.userId()), codeCaptor.capture(),
          durationCaptor.capture());
      verify(mailEventPublisherPort).publish(
          eventIdCaptor.capture(),
          typeCaptor.capture(),
          toCaptor.capture(),
          modelCaptor.capture()
      );

      assertThat(durationCaptor.getValue()).isEqualTo(expectedDuration);
      assertThat(codeCaptor.getValue()).hasSize(6);
      assertThat(eventIdCaptor.getValue()).isNotBlank();
      assertThat(typeCaptor.getValue()).isEqualTo(MailType.VERIFICATION);
      assertThat(toCaptor.getValue()).isEqualTo(command.email());

      VerificationModel model = (VerificationModel) modelCaptor.getValue();
      assertThat(model.userName()).isEqualTo(command.userName());
      assertThat(model.verificationUrl())
          .startsWith("https://threadly.io/api/auth/verify-email?code=");
      assertThat(model.verificationUrl()).endsWith(codeCaptor.getValue());
    }
  }
}
