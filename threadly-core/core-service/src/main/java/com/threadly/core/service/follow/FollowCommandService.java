package com.threadly.core.service.follow;

import com.google.common.base.Objects;
import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.follow.FollowException;
import com.threadly.commons.exception.user.UserException;
import com.threadly.core.domain.follow.Follow;
import com.threadly.core.domain.follow.FollowStatusType;
import com.threadly.core.domain.notification.NotificationType;
import com.threadly.core.domain.notification.metadata.FollowAcceptMeta;
import com.threadly.core.domain.notification.metadata.FollowMeta;
import com.threadly.core.domain.notification.metadata.FollowRequestMeta;
import com.threadly.core.domain.user.User;
import com.threadly.core.port.user.FetchUserPort;
import com.threadly.core.port.user.follow.FollowCommandPort;
import com.threadly.core.port.user.follow.FollowQueryPort;
import com.threadly.core.service.notification.NotificationService;
import com.threadly.core.service.notification.dto.NotificationPublishCommand;
import com.threadly.core.service.validator.user.UserStatusValidator;
import com.threadly.core.usecase.follow.command.FollowCommandUseCase;
import com.threadly.core.usecase.follow.command.dto.FollowRelationCommand;
import com.threadly.core.usecase.follow.command.dto.FollowUserApiResponse;
import com.threadly.core.usecase.follow.command.dto.FollowUserCommand;
import com.threadly.core.usecase.follow.command.dto.HandleFollowRequestCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 팔로우 관련 command 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FollowCommandService implements FollowCommandUseCase {

  private final FollowCommandPort followCommandPort;
  private final FollowQueryPort followQueryPort;

  private final FetchUserPort fetchUserPort;

  private final UserStatusValidator userStatusValidator;

  private final NotificationService notificationService;

  @Transactional
  @Override
  public FollowUserApiResponse followUser(FollowUserCommand command) {
    /*userId 검증*/
    /*userId와 targetId가 일치 하는 경우*/
    if (Objects.equal(command.userId(), command.targetUserId())) {
      throw new UserException(ErrorCode.SELF_FOLLOW_REQUEST_NOT_ALLOWED);
    }

    /*targetId가 존재하지 않는 경우*/
    User targetUser = fetchUserPort.findByUserId(command.targetUserId())
        .orElseThrow(() -> new UserException(
            ErrorCode.USER_NOT_FOUND));

    /*targetUser 상태 검증*/
    userStatusValidator.validateUserStatusWithException(targetUser.getUserId());

    /*follow 도메인 생성*/
    Follow follow = Follow.createFollow(command.userId(), targetUser.getUserId());

    /*targetUser가 공개 계정인 경우*/
    if (!targetUser.isPrivate()) {
      /*APPROVED 처리*/
      follow.markAsApproved();
    }

    /*저장*/
    followCommandPort.createFollow(follow);

    log.info("팔로우 요청 : {} -> {}", follow.getFollowerId(), follow.getFollowingId());


    /*알림 이벤트 발행*/
    notificationService.publish(
        new NotificationPublishCommand(
            follow.getFollowingId(),
            follow.getFollowerId(),
            targetUser.isPrivate() ? NotificationType.FOLLOW_REQUEST : NotificationType.FOLLOW,
            targetUser.isPrivate() ? new FollowRequestMeta() : new FollowMeta()
        )
    );

    /*응답 리턴*/
    return new FollowUserApiResponse(
        follow.getStatusType()
    );
  }

  @Transactional
  @Override
  public void approveFollowRequest(HandleFollowRequestCommand command) {
    /*팔로우 요청 조회*/
    Follow follow = followQueryPort.findByIdAndStatusType(command.followId(),
            FollowStatusType.PENDING)
        .orElseThrow(() -> new FollowException(ErrorCode.FOLLOW_RELATION_NOT_FOUND));

    /*내가 받은 팔로우 요청이 아닌 경우*/
    if (!follow.getFollowingId().equals(command.userId())) {
      throw new FollowException(ErrorCode.FOLLOW_REQUEST_FORBIDDEN);
    }

    /*팔로우 요청 수락 처리*/
    follow.markAsApproved();

    /*업데이트*/
    followCommandPort.updateFollowStatus(follow);
    log.info("팔로우 요청 수락 : {} -> {}", follow.getFollowerId(), follow.getFollowingId());

    /*알림 이벤트 발행*/
    notificationService.publish(
        new NotificationPublishCommand(
            follow.getFollowingId(),
            follow.getFollowerId(),
            NotificationType.FOLLOW_ACCEPT,
            new FollowAcceptMeta()
        )
    );

  }

  @Transactional
  @Override
  public void rejectFollowRequest(HandleFollowRequestCommand command) {
    /*팔로우 요청 조회*/
    Follow follow = followQueryPort.findByIdAndStatusType(command.followId(),
            FollowStatusType.PENDING)
        .orElseThrow(() -> new FollowException(ErrorCode.FOLLOW_RELATION_NOT_FOUND));

    /*내가 받은 팔로우 요청이 아닌 경우*/
    if (!follow.getFollowingId().equals(command.userId())) {
      throw new FollowException(ErrorCode.FOLLOW_REQUEST_FORBIDDEN);
    }

    /*삭제 처리*/
    followCommandPort.deleteFollow(command.followId());
    log.info("팔로우 요청 거절 : {} -> {}", follow.getFollowerId(), follow.getFollowingId());
  }

  @Transactional
  @Override
  public void cancelFollowRequest(FollowRelationCommand command) {
    /*command 검증*/
    validateFollow(command.userId(), command.targetUserId(), FollowStatusType.PENDING);

    /*팔로우 요청 삭제*/
    followCommandPort.deleteByFollowerIdAndFollowingIdAndStatusType(
        command.userId(), command.targetUserId(), FollowStatusType.PENDING
    );
    log.info("팔로우 요청 삭제 : {} -> {}", command.userId(), command.targetUserId());
  }


  @Transactional
  @Override
  public void unfollowUser(FollowRelationCommand command) {
    /*command 검증*/
    validateFollow(command.userId(), command.targetUserId(), FollowStatusType.APPROVED);

    /*팔로잉 삭제*/
    followCommandPort.deleteByFollowerIdAndFollowingIdAndStatusType(
        command.userId(), command.targetUserId(), FollowStatusType.APPROVED
    );
    log.info("팔로잉 삭제 : {} -> {}", command.userId(), command.targetUserId());
  }

  @Transactional
  @Override
  public void removeFollower(FollowRelationCommand command) {
    /*command 검증*/
    validateFollow(command.targetUserId(), command.userId(), FollowStatusType.APPROVED);

    /*팔로워 삭제*/
    followCommandPort.deleteByFollowerIdAndFollowingIdAndStatusType(
        command.targetUserId(), command.userId(), FollowStatusType.APPROVED
    );
    log.info("팔로워 삭제 : {} -> {}", command.targetUserId(), command.userId());
  }

  /**
   * 주어진 commnad에 해당하는 팔로우 조회 후 존재 하지 않을 경우 예외 발생
   *
   * @param followerId
   * @param followingId
   * @param followStatusType
   * @throws FollowException
   */
  private void validateFollow(String followerId, String followingId,
      FollowStatusType followStatusType) {
    /*팔로우 요청 조회*/
    boolean exists = followQueryPort.existsByFollowerIdAndFollowingIdAndStatusType(followerId,
        followingId, followStatusType);

    /*팔로우 요청이 존재하지 않는 경우*/
    if (!exists) {
      throw new FollowException(ErrorCode.FOLLOW_RELATION_NOT_FOUND);
    }
  }
}
