package com.threadly.core.service.validator.follow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.follow.FollowException;
import com.threadly.commons.exception.user.UserException;
import com.threadly.core.domain.follow.Follow;
import com.threadly.core.domain.follow.FollowStatus;
import com.threadly.core.port.follow.out.FollowQueryPort;
import com.threadly.core.port.user.out.UserQueryPort;
import com.threadly.core.service.follow.validator.FollowValidator;
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
class FollowValidatorTest {

  @InjectMocks
  private FollowValidator followValidator;

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
      FollowStatus status = followValidator.validateProfileAccessibleWithException(userId,
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
      assertThatThrownBy(() -> followValidator.validateProfileAccessibleWithException(
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
      FollowStatus status = followValidator.validateProfileAccessibleWithException(userId,
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
      FollowStatus status = followValidator.validateProfileAccessibleWithException(userId,
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
      FollowStatus status = followValidator.validateProfileAccessible(userId, userId);

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
      FollowStatus status = followValidator.validateProfileAccessible(userId, targetUserId);

      //then
      assertThat(status).isEqualTo(FollowStatus.NONE);
      verify(followQueryPort).findFollowStatusType(userId, targetUserId);
    }
  }

  @Order(3)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("팔로우 요청 조회")
  class GetPendingFollowOrThrowTest {

    /*[Case #1] PENDING 팔로우 조회 성공*/
    @Order(1)
    @DisplayName("1. PENDING 상태의 팔로우가 존재하는 경우 조회 성공")
    @Test
    void getPendingFollowOrThrow_shouldReturnFollow_whenExists() throws Exception {
      //given
      String followId = "follow-1";
      Follow follow = Follow.createFollow("user1", "user2");
      when(followQueryPort.findByIdAndStatusType(followId, FollowStatus.PENDING))
          .thenReturn(Optional.of(follow));

      //when
      Follow result = followValidator.getPendingFollowOrThrow(followId);

      //then
      assertThat(result).isNotNull();
      assertThat(result.getStatusType()).isEqualTo(FollowStatus.PENDING);
      verify(followQueryPort).findByIdAndStatusType(followId, FollowStatus.PENDING);
    }

    /*[Case #2] PENDING 팔로우 조회 실패*/
    @Order(2)
    @DisplayName("2. PENDING 상태의 팔로우가 존재하지 않는 경우 예외 발생")
    @Test
    void getPendingFollowOrThrow_shouldThrow_whenNotExists() throws Exception {
      //given
      String followId = "follow-1";
      when(followQueryPort.findByIdAndStatusType(followId, FollowStatus.PENDING))
          .thenReturn(Optional.empty());

      //when & then
      assertThatThrownBy(() -> followValidator.getPendingFollowOrThrow(followId))
          .isInstanceOf(FollowException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.FOLLOW_RELATION_NOT_FOUND);
    }
  }

  @Order(4)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("팔로우 요청 수신자 검증")
  class ValidateFollowRequestReceiverTest {

    /*[Case #1] 팔로우 요청 수신자 일치*/
    @Order(1)
    @DisplayName("1. followingId와 userId가 일치하는 경우 예외가 발생하지 않음")
    @Test
    void validateFollowRequestReceiver_shouldPass_whenMatches() throws Exception {
      //given
      String followingId = "user-1";
      String userId = "user-1";

      //when & then
      assertThatCode(() -> followValidator.validateFollowRequestReceiver(followingId, userId))
          .doesNotThrowAnyException();
    }

    /*[Case #2] 팔로우 요청 수신자 불일치*/
    @Order(2)
    @DisplayName("2. followingId와 userId가 일치하지 않는 경우 예외 발생")
    @Test
    void validateFollowRequestReceiver_shouldThrow_whenNotMatches() throws Exception {
      //given
      String followingId = "user-1";
      String userId = "user-2";

      //when & then
      assertThatThrownBy(
          () -> followValidator.validateFollowRequestReceiver(followingId, userId))
          .isInstanceOf(FollowException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.FOLLOW_REQUEST_FORBIDDEN);
    }
  }

  @Order(5)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("자기 자신 팔로우 검증")
  class ValidateNotSelfFollowTest {

    /*[Case #1] 다른 사용자 팔로우 - 정상*/
    @Order(1)
    @DisplayName("1. userId와 targetUserId가 다른 경우 예외가 발생하지 않음")
    @Test
    void validateNotSelfFollow_shouldPass_whenDifferentUsers() throws Exception {
      //given
      String userId = "user-1";
      String targetUserId = "user-2";

      //when & then
      assertThatCode(() -> followValidator.validateNotSelfFollow(userId, targetUserId))
          .doesNotThrowAnyException();
    }

    /*[Case #2] 자기 자신 팔로우 시도 - 예외 발생*/
    @Order(2)
    @DisplayName("2. userId와 targetUserId가 같은 경우 예외 발생")
    @Test
    void validateNotSelfFollow_shouldThrow_whenSameUser() throws Exception {
      //given
      String userId = "user-1";
      String targetUserId = "user-1";

      //when & then
      assertThatThrownBy(() -> followValidator.validateNotSelfFollow(userId, targetUserId))
          .isInstanceOf(FollowException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.SELF_FOLLOW_REQUEST_NOT_ALLOWED);
    }
  }

  @Order(6)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("팔로우 존재 여부 검증")
  class ValidateFollowExistsTest {

    /*[Case #1] 팔로우 존재 - 정상*/
    @Order(1)
    @DisplayName("1. 팔로우 관계가 존재하는 경우 예외가 발생하지 않음")
    @Test
    void validateFollowExists_shouldPass_whenExists() throws Exception {
      //given
      String followerId = "user-1";
      String followingId = "user-2";
      FollowStatus status = FollowStatus.APPROVED;
      when(followQueryPort.existsByFollowerIdAndFollowingIdAndStatusType(followerId, followingId,
          status))
          .thenReturn(true);

      //when & then
      assertThatCode(
          () -> followValidator.validateFollowExists(followerId, followingId, status))
          .doesNotThrowAnyException();
      verify(followQueryPort).existsByFollowerIdAndFollowingIdAndStatusType(followerId,
          followingId, status);
    }

    /*[Case #2] 팔로우 미존재 - 예외 발생*/
    @Order(2)
    @DisplayName("2. 팔로우 관계가 존재하지 않는 경우 예외 발생")
    @Test
    void validateFollowExists_shouldThrow_whenNotExists() throws Exception {
      //given
      String followerId = "user-1";
      String followingId = "user-2";
      FollowStatus status = FollowStatus.APPROVED;
      when(followQueryPort.existsByFollowerIdAndFollowingIdAndStatusType(followerId, followingId,
          status))
          .thenReturn(false);

      //when & then
      assertThatThrownBy(() -> followValidator.validateFollowExists(followerId, followingId, status))
          .isInstanceOf(FollowException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.FOLLOW_RELATION_NOT_FOUND);
    }
  }
}
