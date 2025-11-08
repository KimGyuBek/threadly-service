package com.threadly.core.service.follow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.commons.exception.follow.FollowException;
import com.threadly.commons.exception.user.UserException;
import com.threadly.core.domain.follow.Follow;
import com.threadly.core.domain.follow.FollowStatus;
import com.threadly.core.domain.user.User;
import com.threadly.core.port.follow.in.command.dto.FollowRelationCommand;
import com.threadly.core.port.follow.in.command.dto.FollowUserApiResponse;
import com.threadly.core.port.follow.in.command.dto.FollowUserCommand;
import com.threadly.core.port.follow.in.command.dto.HandleFollowRequestCommand;
import com.threadly.core.port.follow.out.FollowCommandPort;
import com.threadly.core.port.follow.out.FollowQueryPort;
import com.threadly.core.port.user.out.UserQueryPort;
import com.threadly.core.service.notification.dto.NotificationPublishCommand;
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
 * FollowCommandService 테스트
 */
@ExtendWith(MockitoExtension.class)
class FollowCommandServiceTest {

  @InjectMocks
  private FollowCommandService followCommandService;

  @Mock
  private FollowCommandPort followCommandPort;

  @Mock
  private FollowQueryPort followQueryPort;

  @Mock
  private UserQueryPort userQueryPort;

  @Mock
  private UserValidator userValidator;

  @Mock
  private ApplicationEventPublisher applicationEventPublisher;

  @Nested
  @DisplayName("팔로우 요청 테스트")
  class FollowUserTest {

    /*[Case #1] 팔로우 성공 - 공개 계정 (APPROVED 상태)*/
    @DisplayName("팔로우 성공 - 공개 계정인 경우 APPROVED 상태로 생성되어야 한다")
    @Test
    public void followUser_shouldCreateApprovedFollow_whenTargetIsPublic() throws Exception {
      //given
      FollowUserCommand command = new FollowUserCommand("user1", "user2");

      User targetUser = User.newUser("user2", "password", "test@test.com", "010-1234-5678");
      targetUser.markAsPublic();

      when(userQueryPort.findByUserId(command.targetUserId())).thenReturn(
          Optional.of(targetUser));

      //when
      FollowUserApiResponse result = followCommandService.followUser(command);

      //then
      assertThat(result.followStatus()).isEqualTo(FollowStatus.APPROVED);
      verify(followCommandPort).createFollow(any(Follow.class));
      verify(applicationEventPublisher).publishEvent(any(NotificationPublishCommand.class));
    }

    /*[Case #2] 팔로우 성공 - 비공개 계정 (PENDING 상태)*/
    @DisplayName("팔로우 성공 - 비공개 계정인 경우 PENDING 상태로 생성되어야 한다")
    @Test
    public void followUser_shouldCreatePendingFollow_whenTargetIsPrivate() throws Exception {
      //given
      FollowUserCommand command = new FollowUserCommand("user1", "user2");

      User targetUser = User.newUser("user2", "password", "test@test.com", "010-1234-5678");
      targetUser.markAsPrivate();

      when(userQueryPort.findByUserId(command.targetUserId())).thenReturn(
          Optional.of(targetUser));

      //when
      FollowUserApiResponse result = followCommandService.followUser(command);

      //then
      assertThat(result.followStatus()).isEqualTo(FollowStatus.PENDING);
      verify(followCommandPort).createFollow(any(Follow.class));
      verify(applicationEventPublisher).publishEvent(any(NotificationPublishCommand.class));
    }

    /*[Case #3] 팔로우 실패 - 자기 자신을 팔로우*/
    @DisplayName("팔로우 실패 - 자기 자신을 팔로우하는 경우 예외가 발생해야 한다")
    @Test
    public void followUser_shouldThrowException_whenTargetIsSelf() throws Exception {
      //given
      FollowUserCommand command = new FollowUserCommand("user1", "user1");

      //when & then
      assertThrows(UserException.class, () -> followCommandService.followUser(command));
    }

    /*[Case #4] 팔로우 실패 - 존재하지 않는 사용자*/
    @DisplayName("팔로우 실패 - 존재하지 않는 사용자인 경우 예외가 발생해야 한다")
    @Test
    public void followUser_shouldThrowException_whenTargetNotFound() throws Exception {
      //given
      FollowUserCommand command = new FollowUserCommand("user1", "user2");

      when(userQueryPort.findByUserId(command.targetUserId())).thenReturn(Optional.empty());

      //when & then
      assertThrows(UserException.class, () -> followCommandService.followUser(command));
    }
  }

  @Nested
  @DisplayName("팔로우 요청 수락 테스트")
  class ApproveFollowRequestTest {

    /*[Case #1] 팔로우 요청 수락 성공 - APPROVED 상태로 변경*/
    @DisplayName("팔로우 요청 수락 성공 - 상태가 APPROVED로 변경되어야 한다")
    @Test
    public void approveFollowRequest_shouldChangeStatusToApproved() throws Exception {
      //given
      HandleFollowRequestCommand command = new HandleFollowRequestCommand("user2", "follow1");

      Follow follow = Follow.createFollow("user1", "user2");
      when(followQueryPort.findByIdAndStatusType(command.followId(),
          FollowStatus.PENDING)).thenReturn(Optional.of(follow));

      //when
      followCommandService.approveFollowRequest(command);

      //then
      verify(followCommandPort).updateFollowStatus(any(Follow.class));
      verify(applicationEventPublisher).publishEvent(any(NotificationPublishCommand.class));
    }

    /*[Case #2] 팔로우 요청 수락 실패 - 존재하지 않는 요청*/
    @DisplayName("팔로우 요청 수락 실패 - 존재하지 않는 요청인 경우 예외가 발생해야 한다")
    @Test
    public void approveFollowRequest_shouldThrowException_whenRequestNotFound() throws Exception {
      //given
      HandleFollowRequestCommand command = new HandleFollowRequestCommand("user2", "follow1");

      when(followQueryPort.findByIdAndStatusType(command.followId(),
          FollowStatus.PENDING)).thenReturn(Optional.empty());

      //when & then
      assertThrows(FollowException.class,
          () -> followCommandService.approveFollowRequest(command));
    }

    /*[Case #3] 팔로우 요청 수락 실패 - 다른 사용자의 요청*/
    @DisplayName("팔로우 요청 수락 실패 - 다른 사용자의 요청인 경우 예외가 발생해야 한다")
    @Test
    public void approveFollowRequest_shouldThrowException_whenNotMyRequest() throws Exception {
      //given
      HandleFollowRequestCommand command = new HandleFollowRequestCommand("user3", "follow1");

      Follow follow = Follow.createFollow("user1", "user2");
      when(followQueryPort.findByIdAndStatusType(command.followId(),
          FollowStatus.PENDING)).thenReturn(Optional.of(follow));

      //when & then
      assertThrows(FollowException.class,
          () -> followCommandService.approveFollowRequest(command));
    }
  }

  @Nested
  @DisplayName("언팔로우 테스트")
  class UnfollowUserTest {

    /*[Case #1] 언팔로우 성공 - 팔로우 관계 삭제*/
    @DisplayName("언팔로우 성공 - 팔로우 관계가 삭제되어야 한다")
    @Test
    public void unfollowUser_shouldDeleteFollow() throws Exception {
      //given
      FollowRelationCommand command = new FollowRelationCommand("user1", "user2");

      when(followQueryPort.existsByFollowerIdAndFollowingIdAndStatusType(
          command.userId(), command.targetUserId(), FollowStatus.APPROVED
      )).thenReturn(true);

      //when
      followCommandService.unfollowUser(command);

      //then
      verify(followCommandPort).deleteByFollowerIdAndFollowingIdAndStatusType(
          command.userId(), command.targetUserId(), FollowStatus.APPROVED
      );
    }

    /*[Case #2] 언팔로우 실패 - 팔로우 관계가 존재하지 않음*/
    @DisplayName("언팔로우 실패 - 팔로우 관계가 존재하지 않는 경우 예외가 발생해야 한다")
    @Test
    public void unfollowUser_shouldThrowException_whenFollowNotExists() throws Exception {
      //given
      FollowRelationCommand command = new FollowRelationCommand("user1", "user2");

      when(followQueryPort.existsByFollowerIdAndFollowingIdAndStatusType(
          command.userId(), command.targetUserId(), FollowStatus.APPROVED
      )).thenReturn(false);

      //when & then
      assertThrows(FollowException.class, () -> followCommandService.unfollowUser(command));
    }
  }

  @Nested
  @DisplayName("팔로우 요청 취소 테스트")
  class CancelFollowRequestTest {

    /*[Case #1] 팔로우 요청 취소 성공*/
    @DisplayName("팔로우 요청 취소 성공 - 팔로우 요청이 삭제되어야 한다")
    @Test
    public void cancelFollowRequest_shouldDeleteRequest() throws Exception {
      //given
      FollowRelationCommand command = new FollowRelationCommand("user1", "user2");

      when(followQueryPort.existsByFollowerIdAndFollowingIdAndStatusType(
          command.userId(), command.targetUserId(), FollowStatus.PENDING
      )).thenReturn(true);

      //when
      followCommandService.cancelFollowRequest(command);

      //then
      verify(followCommandPort).deleteByFollowerIdAndFollowingIdAndStatusType(
          command.userId(), command.targetUserId(), FollowStatus.PENDING
      );
    }
  }
}
