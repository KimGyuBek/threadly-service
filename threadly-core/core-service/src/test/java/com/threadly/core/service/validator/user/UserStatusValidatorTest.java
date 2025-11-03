package com.threadly.core.service.validator.user;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.user.UserException;
import com.threadly.core.domain.user.UserStatus;
import com.threadly.core.port.user.out.UserQueryPort;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UserStatusValidator 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class UserStatusValidatorTest {

  @InjectMocks
  private UserStatusValidator userStatusValidator;

  @Mock
  private UserQueryPort userQueryPort;

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    /*[Case #1] 활성 사용자 상태 검증*/
    @Order(1)
    @DisplayName("1. ACTIVE 상태인 경우 예외가 발생하지 않는지 검증")
    @Test
    void validateUserStatusWithException_shouldPass_whenActive() throws Exception {
      //given
      String userId = "user-1";
      when(userQueryPort.getUserStatus(userId)).thenReturn(Optional.of(UserStatus.ACTIVE));

      //when & then
      assertThatCode(() -> userStatusValidator.validateUserStatusWithException(userId))
          .doesNotThrowAnyException();
      verify(userQueryPort).getUserStatus(userId);
    }
  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

    /*[Case #1] 사용자 상태 미존재*/
    @Order(1)
    @DisplayName("1. 사용자 상태가 조회되지 않으면 예외가 발생하는지 검증")
    @Test
    void validateUserStatusWithException_shouldThrow_whenUserNotFound() throws Exception {
      //given
      String userId = "user-unknown";
      when(userQueryPort.getUserStatus(userId)).thenReturn(Optional.empty());

      //when & then
      assertThatThrownBy(() -> userStatusValidator.validateUserStatusWithException(userId))
          .isInstanceOf(UserException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    /*[Case #2] 삭제된 사용자*/
    @Order(2)
    @DisplayName("2. DELETED 상태인 경우 예외가 발생하는지 검증")
    @Test
    void validateUserStatusWithException_shouldThrow_whenDeleted() throws Exception {
      //given
      String userId = "user-1";
      when(userQueryPort.getUserStatus(userId)).thenReturn(Optional.of(UserStatus.DELETED));

      //when & then
      assertThatThrownBy(() -> userStatusValidator.validateUserStatusWithException(userId))
          .isInstanceOf(UserException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.USER_ALREADY_DELETED);
    }

    /*[Case #3] 프로필 미완성 사용자*/
    @Order(3)
    @DisplayName("3. INCOMPLETE_PROFILE 상태인 경우 예외가 발생하는지 검증")
    @Test
    void validateUserStatusWithException_shouldThrow_whenIncompleteProfile() throws Exception {
      //given
      String userId = "user-1";
      when(userQueryPort.getUserStatus(userId)).thenReturn(Optional.of(UserStatus.INCOMPLETE_PROFILE));

      //when & then
      assertThatThrownBy(() -> userStatusValidator.validateUserStatusWithException(userId))
          .isInstanceOf(UserException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.USER_PROFILE_NOT_SET);
    }

    /*[Case #4] 비활성 사용자*/
    @Order(4)
    @DisplayName("4. INACTIVE 상태인 경우 예외가 발생하는지 검증")
    @Test
    void validateUserStatusWithException_shouldThrow_whenInactive() throws Exception {
      //given
      String userId = "user-1";
      when(userQueryPort.getUserStatus(userId)).thenReturn(Optional.of(UserStatus.INACTIVE));

      //when & then
      assertThatThrownBy(() -> userStatusValidator.validateUserStatusWithException(userId))
          .isInstanceOf(UserException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.USER_INACTIVE);
    }
  }
}
