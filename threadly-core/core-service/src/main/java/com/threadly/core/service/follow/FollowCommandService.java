package com.threadly.core.service.follow;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.follow.FollowException;
import com.threadly.core.domain.follow.Follow;
import com.threadly.core.domain.follow.FollowStatus;
import com.threadly.core.domain.notification.NotificationType;
import com.threadly.core.domain.notification.metadata.FollowAcceptMeta;
import com.threadly.core.domain.notification.metadata.FollowMeta;
import com.threadly.core.domain.notification.metadata.FollowRequestMeta;
import com.threadly.core.domain.user.User;
import com.threadly.core.port.follow.in.command.FollowCommandUseCase;
import com.threadly.core.port.follow.in.command.dto.FollowRelationCommand;
import com.threadly.core.port.follow.in.command.dto.FollowUserApiResponse;
import com.threadly.core.port.follow.in.command.dto.FollowUserCommand;
import com.threadly.core.port.follow.in.command.dto.HandleFollowRequestCommand;
import com.threadly.core.port.follow.out.FollowCommandPort;
import com.threadly.core.service.notification.dto.NotificationPublishCommand;
import com.threadly.core.service.follow.validator.FollowValidator;
import com.threadly.core.service.user.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 팔로우 관련 command 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FollowCommandService implements FollowCommandUseCase {

  private final UserValidator userValidator;
  private final FollowValidator followValidator;

  private final FollowCommandPort followCommandPort;

  private final ApplicationEventPublisher applicationEventPublisher;

  @Transactional
  @Override
  public FollowUserApiResponse followUser(FollowUserCommand command) {
    /*userId와 targetUserId가 일치하는지 검증*/
    followValidator.validateNotSelfFollow(command.userId(), command.targetUserId());

    /*targetId가 존재하지 않는 경우*/
    User targetUser = userValidator.getUserByIdOrElseThrow(command.targetUserId());

    /*targetUser 상태 검증*/
    userValidator.validateUserStatusWithException(targetUser.getUserId());

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
    applicationEventPublisher.publishEvent(
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
    Follow follow = followValidator.getPendingFollowOrThrow(
        command.followId());

    /*내가 받은 팔로우 요청이 아닌 경우*/
    followValidator.validateFollowRequestReceiver(follow.getFollowingId(), command.userId());

    /*팔로우 요청 수락 처리*/
    follow.markAsApproved();
    followCommandPort.updateFollowStatus(follow);

    log.info("팔로우 요청 수락 : {} -> {}", follow.getFollowerId(), follow.getFollowingId());

    /*알림 이벤트 발행*/
    applicationEventPublisher.publishEvent(
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
    Follow follow = followValidator.getPendingFollowOrThrow(command.followId());

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
    followValidator.validateFollowExists(command.userId(), command.targetUserId(),
        FollowStatus.PENDING);

    /*팔로우 요청 삭제*/
    followCommandPort.deleteByFollowerIdAndFollowingIdAndStatusType(
        command.userId(), command.targetUserId(), FollowStatus.PENDING
    );
    log.info("팔로우 요청 삭제 : {} -> {}", command.userId(), command.targetUserId());
  }


  @Transactional
  @Override
  public void unfollowUser(FollowRelationCommand command) {
    /*command 검증*/
    followValidator.validateFollowExists(command.userId(), command.targetUserId(),
        FollowStatus.APPROVED);

    /*팔로잉 삭제*/
    followCommandPort.deleteByFollowerIdAndFollowingIdAndStatusType(
        command.userId(), command.targetUserId(), FollowStatus.APPROVED
    );
    log.info("팔로잉 삭제 : {} -> {}", command.userId(), command.targetUserId());
  }

  @Transactional
  @Override
  public void removeFollower(FollowRelationCommand command) {
    /*command 검증*/
    followValidator.validateFollowExists(command.targetUserId(), command.userId(),
        FollowStatus.APPROVED);

    /*팔로워 삭제*/
    followCommandPort.deleteByFollowerIdAndFollowingIdAndStatusType(
        command.targetUserId(), command.userId(), FollowStatus.APPROVED
    );
    log.info("팔로워 삭제 : {} -> {}", command.targetUserId(), command.userId());
  }

}
