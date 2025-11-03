package com.threadly.core.service.verification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.mail.EmailVerificationException;
import com.threadly.commons.exception.user.UserException;
import com.threadly.core.domain.mail.MailType;
import com.threadly.core.domain.user.User;
import com.threadly.core.domain.user.UserRoleType;
import com.threadly.core.domain.user.UserStatus;
import com.threadly.core.port.mail.in.SendMailCommand;
import com.threadly.core.port.user.out.UserCommandPort;
import com.threadly.core.port.user.out.UserQueryPort;
import com.threadly.core.port.verification.EmailVerificationPort;
import java.util.Optional;
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
import org.springframework.context.ApplicationEventPublisher;

/**
 * EmailVerificationService 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceTest {

  @InjectMocks
  private EmailVerificationService emailVerificationService;

  @Mock
  private EmailVerificationPort emailVerificationPort;

  @Mock
  private UserCommandPort userCommandPort;

  @Mock
  private UserQueryPort userQueryPort;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    /*[Case #1] 이메일 인증 성공*/
    @Order(1)
    @DisplayName("1. 이메일 인증에 성공하면 상태가 업데이트되고 환영 메일이 발송되는지 검증")
    @Test
    void verifyEmail_shouldUpdateAndPublishWelcomeMail() throws Exception {
      //given
      String code = "ABC123";
      String userId = "user-1";
      User user = User.builder()
          .userId(userId)
          .userName("사용자")
          .password("encoded")
          .email("test@threadly.io")
          .phone("010-0000-0000")
          .userRoleType(UserRoleType.USER)
          .userStatus(UserStatus.INCOMPLETE_PROFILE)
          .isEmailVerified(false)
          .isPrivate(false)
          .build();

      when(emailVerificationPort.getUserId(code)).thenReturn(userId);
      when(userQueryPort.findByUserId(userId)).thenReturn(Optional.of(user));

      ArgumentCaptor<SendMailCommand> mailCaptor = ArgumentCaptor.forClass(SendMailCommand.class);

      //when
      emailVerificationService.verifyEmail(code);

      //then
      assertThat(user.isEmailVerified()).isTrue();

      verify(userCommandPort).updateEmailVerification(userId, true);
      verify(emailVerificationPort).deleteCode(code);
      verify(eventPublisher).publishEvent(mailCaptor.capture());

      SendMailCommand command = mailCaptor.getValue();
      assertThat(command.userId()).isEqualTo(userId);
      assertThat(command.mailType()).isEqualTo(MailType.WELCOME);
    }
  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

    /*[Case #1] 사용자 미존재*/
    @Order(1)
    @DisplayName("1. 인증 대상 사용자가 존재하지 않으면 예외가 발생하는지 검증")
    @Test
    void verifyEmail_shouldThrow_whenUserNotFound() throws Exception {
      //given
      String code = "ABC123";
      when(emailVerificationPort.getUserId(code)).thenReturn("user-unknown");
      when(userQueryPort.findByUserId("user-unknown")).thenReturn(Optional.empty());

      //when & then
      assertThatThrownBy(() -> emailVerificationService.verifyEmail(code))
          .isInstanceOf(UserException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.USER_NOT_FOUND);

      verify(userCommandPort, never()).updateEmailVerification(any(), any(Boolean.class));
      verify(eventPublisher, never()).publishEvent(any());
    }

    /*[Case #2] 이미 인증된 사용자*/
    @Order(2)
    @DisplayName("2. 이미 인증된 사용자라면 예외가 발생하는지 검증")
    @Test
    void verifyEmail_shouldThrow_whenAlreadyVerified() throws Exception {
      //given
      String code = "ABC123";
      String userId = "user-1";
      User user = User.builder()
          .userId(userId)
          .userName("사용자")
          .password("encoded")
          .email("test@threadly.io")
          .phone("010-0000-0000")
          .userRoleType(UserRoleType.USER)
          .userStatus(UserStatus.ACTIVE)
          .isEmailVerified(true)
          .isPrivate(false)
          .build();

      when(emailVerificationPort.getUserId(code)).thenReturn(userId);
      when(userQueryPort.findByUserId(userId)).thenReturn(Optional.of(user));

      //when & then
      assertThatThrownBy(() -> emailVerificationService.verifyEmail(code))
          .isInstanceOf(EmailVerificationException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.EMAIL_ALREADY_VERIFIED);

      verify(userCommandPort, never()).updateEmailVerification(any(), any(Boolean.class));
      verify(emailVerificationPort, never()).deleteCode(code);
      verify(eventPublisher, never()).publishEvent(any());
    }
  }
}
