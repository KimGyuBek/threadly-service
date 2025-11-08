package com.threadly.core.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.commons.exception.user.UserException;
import com.threadly.commons.properties.TtlProperties;
import com.threadly.core.domain.user.User;
import com.threadly.core.domain.user.UserRoleType;
import com.threadly.core.domain.user.UserStatus;
import com.threadly.core.port.mail.in.SendMailCommand;
import com.threadly.core.port.token.out.TokenCommandPort;
import com.threadly.core.port.user.in.account.command.dto.RegisterUserApiResponse;
import com.threadly.core.port.user.in.account.command.dto.RegisterUserCommand;
import com.threadly.core.port.user.out.UserCommandPort;
import com.threadly.core.port.user.out.UserQueryPort;
import com.threadly.core.port.user.out.UserResult;
import com.threadly.core.service.notification.dto.NotificationPublishCommand;
import com.threadly.core.service.processor.TokenProcessor;
import com.threadly.core.service.validator.user.UserValidator;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

/**
 * UserCommandService 테스트
 */
@ExtendWith(MockitoExtension.class)
class UserCommandServiceTest {

  @InjectMocks
  private UserCommandService userCommandService;

  @Mock
  private UserQueryPort userQueryPort;

  @Mock
  private UserCommandPort userCommandPort;

  @Mock
  private TokenCommandPort tokenCommandPort;

  @Mock
  private ApplicationEventPublisher applicationEventPublisher;

  @Mock
  private UserValidator userValidator;

  @Mock
  private TokenProcessor tokenProcessor;

  @Mock
  private TtlProperties ttlProperties;

  @Nested
  @DisplayName("회원가입 테스트")
  class RegisterTest {

    /*[Case #1] 회원가입 성공 - 새로운 사용자 생성*/
    @DisplayName("회원가입 성공 - 새로운 사용자가 생성되어야 한다")
    @Test
    public void register_shouldCreateNewUser_whenEmailNotExists() throws Exception {
      //given
      RegisterUserCommand command = new RegisterUserCommand(
          "test@test.com",
          "username",
          "password",
          "010-1234-5678"
      );

//      when(userQueryPort.findByEmail(command.getEmail())).thenReturn(Optional.empty());
      when(userQueryPort.existsByEmail(command.getEmail())).thenReturn(false);
      when(userCommandPort.save(any(User.class))).thenReturn(
          UserResult.builder()
              .userId("user1")
              .userName("username")
              .userRoleType(UserRoleType.USER)
              .email("test@test.com")
              .userStatus(UserStatus.INCOMPLETE_PROFILE)
              .isEmailVerified(false)
              .build()
      );

      //when
      RegisterUserApiResponse result = userCommandService.register(command);

      //then
      assertAll(
          () -> assertThat(result.getUserId()).isEqualTo("user1"),
          () -> assertThat(result.getUserName()).isEqualTo("username"),
          () -> assertThat(result.getEmail()).isEqualTo("test@test.com"),
          () -> assertThat(result.getUserStatus()).isEqualTo(UserStatus.INCOMPLETE_PROFILE)
      );

      verify(userCommandPort).save(any(User.class));

      /*인증 메일 전송 이벤트 발행 검증*/
      verify(applicationEventPublisher).publishEvent(any(SendMailCommand.class));
    }

    /*[Case #2] 회원가입 실패 - 이미 존재하는 이메일*/
    @DisplayName("회원가입 실패 - 이미 존재하는 이메일인 경우 예외가 발생해야 한다")
    @Test
    public void register_shouldThrowException_whenEmailAlreadyExists() throws Exception {
      //given
      RegisterUserCommand command = new RegisterUserCommand(
          "test@test.com",
          "username",
          "password",
          "010-1234-5678"
      );

      User existingUser = User.newUser("username", "password", "test@test.com",
          "010-1234-5678");
      when(userQueryPort.existsByEmail(command.getEmail())).thenReturn(true);

      //when & then
      assertThrows(UserException.class, () -> userCommandService.register(command));
    }
  }

  @Nested
  @DisplayName("계정 탈퇴 테스트")
  class WithdrawAccountTest {

    /*[Case #1] 계정 탈퇴 성공 - DELETED 상태로 변경*/
    @DisplayName("계정 탈퇴 성공 - 사용자 상태가 DELETED로 변경되어야 한다")
    @Test
    public void withdrawMyAccount_shouldChangeStatusToDeleted() throws Exception {
      //given
      String userId = "user1";
      String bearerToken = "Bearer token123";

      User user = User.newUser("username", "password", "test@test.com", "010-1234-5678");
      when(userValidator.getUserByIdOrElseThrow(userId)).thenReturn(user);

      //when
      userCommandService.withdrawMyAccount(userId, bearerToken);

      //then
      verify(userCommandPort).updateUserStatus(userId, UserStatus.DELETED);
      verify(tokenProcessor).addBlackListTokenAndDeleteRefreshToken(userId, bearerToken);
    }

    /*[Case #2] 계정 탈퇴 실패 - 존재하지 않는 사용자*/
    @DisplayName("계정 탈퇴 실패 - 존재하지 않는 사용자인 경우 예외가 발생해야 한다")
    @Test
    public void withdrawMyAccount_shouldThrowException_whenUserNotFound() throws Exception {
      //given
      String userId = "user1";
      String bearerToken = "Bearer token123";

      when(userValidator.getUserByIdOrElseThrow(userId))
          .thenThrow(new UserException(com.threadly.commons.exception.ErrorCode.USER_NOT_FOUND));

      //when & then
      assertThrows(UserException.class,
          () -> userCommandService.withdrawMyAccount(userId, bearerToken));
    }
  }

  @Nested
  @DisplayName("계정 비활성화 테스트")
  class DeactivateAccountTest {

    /*[Case #1] 계정 비활성화 성공 - INACTIVE 상태로 변경*/
    @DisplayName("계정 비활성화 성공 - 사용자 상태가 INACTIVE로 변경되어야 한다")
    @Test
    public void deactivateMyAccount_shouldChangeStatusToInactive() throws Exception {
      //given
      String userId = "user1";
      String bearerToken = "Bearer token123";

      User user = User.newUser("username", "password", "test@test.com", "010-1234-5678");
      user.markAsActive();
      when(userValidator.getUserByIdOrElseThrow(userId)).thenReturn(user);

      //when
      userCommandService.deactivateMyAccount(userId, bearerToken);

      //then
      verify(userCommandPort).updateUserStatus(userId, UserStatus.INACTIVE);
      verify(tokenProcessor).addBlackListTokenAndDeleteRefreshToken(userId, bearerToken);
    }
  }

  @Nested
  @DisplayName("비밀번호 변경 테스트")
  class ChangePasswordTest {

    /*[Case #1] 비밀번호 변경 성공*/
    @DisplayName("비밀번호 변경 성공 - 새로운 비밀번호로 변경되어야 한다")
    @Test
    public void changePassword_shouldUpdatePassword() throws Exception {
      //given
      String userId = "user1";
      String newPassword = "newPassword123";

      //when
      userCommandService.changePassword(
          new com.threadly.core.port.user.in.account.command.dto.ChangePasswordCommand(userId,
              newPassword)
      );

      //then
      verify(userCommandPort).changePassword(userId, newPassword);
    }
  }
}
