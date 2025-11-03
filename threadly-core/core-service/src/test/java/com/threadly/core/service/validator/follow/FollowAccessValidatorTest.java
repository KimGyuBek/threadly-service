package com.threadly.core.service.validator.follow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.user.UserException;
import com.threadly.core.domain.follow.FollowStatus;
import com.threadly.core.port.follow.out.FollowQueryPort;
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
 * FollowAccessValidator 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class FollowAccessValidatorTest {

  @InjectMocks
  private FollowAccessValidator followAccessValidator;

  @Mock
  private FollowQueryPort followQueryPort;

  @Mock
  private UserQueryPort userQueryPort;

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("프로필 접근 가능 여부 검증 - 예외 포함")
  class ValidateProfileAccessibleWithExceptionTest {

    /*[Case #1] 자신의 프로필 접근*/
    @Order(1)
    @DisplayName("1. 자신의 프로필인 경우 SELF가 반환되는지 검증")
    @Test
    void validateProfileAccessibleWithException_shouldReturnSelf_whenSameUser() throws Exception {
      //given
      String userId = "user-1";

      //when
      FollowStatus status = followAccessValidator.validateProfileAccessibleWithException(userId,
          userId);

      //then
      assertThat(status).isEqualTo(FollowStatus.SELF);
      verifyNoInteractions(followQueryPort, userQueryPort);
    }

    /*[Case #2] 비공개 사용자 접근 - 팔로우 미승인*/
    @Order(2)
    @DisplayName("2. 비공개 사용자이면서 팔로우가 승인되지 않은 경우 예외가 발생하는지 검증")
    @Test
    void validateProfileAccessibleWithException_shouldThrow_whenPrivateAndNotApproved()
        throws Exception {
      //given
      String userId = "viewer";
      String targetUserId = "target";

      when(followQueryPort.findFollowStatusType(userId, targetUserId))
          .thenReturn(Optional.of(FollowStatus.PENDING));
      when(userQueryPort.isUserPrivate(targetUserId)).thenReturn(true);

      //when & then
      assertThatThrownBy(() -> followAccessValidator.validateProfileAccessibleWithException(
          userId, targetUserId))
          .isInstanceOf(UserException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.USER_PROFILE_PRIVATE);

      verify(followQueryPort).findFollowStatusType(userId, targetUserId);
      verify(userQueryPort).isUserPrivate(targetUserId);
    }

    /*[Case #3] 비공개 사용자 접근 - 팔로우 승인*/
    @Order(3)
    @DisplayName("3. 비공개 사용자이지만 팔로우가 승인된 경우 APPROVED가 반환되는지 검증")
    @Test
    void validateProfileAccessibleWithException_shouldReturnApproved_whenPrivateAndApproved()
        throws Exception {
      //given
      String userId = "viewer";
      String targetUserId = "target";

      when(followQueryPort.findFollowStatusType(userId, targetUserId))
          .thenReturn(Optional.of(FollowStatus.APPROVED));
      when(userQueryPort.isUserPrivate(targetUserId)).thenReturn(true);

      //when
      FollowStatus status = followAccessValidator.validateProfileAccessibleWithException(userId,
          targetUserId);

      //then
      assertThat(status).isEqualTo(FollowStatus.APPROVED);
      verify(followQueryPort).findFollowStatusType(userId, targetUserId);
      verify(userQueryPort).isUserPrivate(targetUserId);
    }

    /*[Case #4] 공개 사용자 접근*/
    @Order(4)
    @DisplayName("4. 공개 사용자 프로필은 팔로우 상태가 그대로 반환되는지 검증")
    @Test
    void validateProfileAccessibleWithException_shouldReturnStatus_whenPublicUser()
        throws Exception {
      //given
      String userId = "viewer";
      String targetUserId = "target";

      when(followQueryPort.findFollowStatusType(userId, targetUserId))
          .thenReturn(Optional.empty());
      when(userQueryPort.isUserPrivate(targetUserId)).thenReturn(false);

      //when
      FollowStatus status = followAccessValidator.validateProfileAccessibleWithException(userId,
          targetUserId);

      //then
      assertThat(status).isEqualTo(FollowStatus.NONE);
      verify(followQueryPort).findFollowStatusType(userId, targetUserId);
      verify(userQueryPort).isUserPrivate(targetUserId);
    }
  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("프로필 접근 가능 여부 검증 - 예외 미포함")
  class ValidateProfileAccessibleTest {

    /*[Case #1] 자신의 프로필 접근*/
    @Order(1)
    @DisplayName("1. 자신의 프로필인 경우 SELF가 반환되는지 검증")
    @Test
    void validateProfileAccessible_shouldReturnSelf_whenSameUser() throws Exception {
      //given
      String userId = "user-1";

      //when
      FollowStatus status = followAccessValidator.validateProfileAccessible(userId, userId);

      //then
      assertThat(status).isEqualTo(FollowStatus.SELF);
      verifyNoInteractions(followQueryPort, userQueryPort);
    }

    /*[Case #2] 팔로우 상태 미존재*/
    @Order(2)
    @DisplayName("2. 팔로우 상태가 없는 경우 NONE이 반환되는지 검증")
    @Test
    void validateProfileAccessible_shouldReturnNone_whenStatusNotFound() throws Exception {
      //given
      String userId = "viewer";
      String targetUserId = "target";
      when(followQueryPort.findFollowStatusType(userId, targetUserId))
          .thenReturn(Optional.empty());

      //when
      FollowStatus status = followAccessValidator.validateProfileAccessible(userId, targetUserId);

      //then
      assertThat(status).isEqualTo(FollowStatus.NONE);
      verify(followQueryPort).findFollowStatusType(userId, targetUserId);
    }
  }
}
