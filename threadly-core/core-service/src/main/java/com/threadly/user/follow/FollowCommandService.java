package com.threadly.user.follow;

import com.google.common.base.Objects;
import com.threadly.exception.ErrorCode;
import com.threadly.exception.follow.FollowException;
import com.threadly.exception.user.UserException;
import com.threadly.follow.Follow;
import com.threadly.follow.FollowStatusType;
import com.threadly.follow.command.FollowCommandUseCase;
import com.threadly.follow.command.dto.FollowUserApiResponse;
import com.threadly.follow.command.dto.FollowUserCommand;
import com.threadly.follow.command.dto.HandleFollowRequestCommand;
import com.threadly.user.FetchUserPort;
import com.threadly.user.User;
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
    switch (targetUser.getUserStatusType()) {
      case ACTIVE:
        break;
      case INACTIVE:
        throw new UserException(ErrorCode.USER_INACTIVE);
      case DELETED:
        throw new UserException(ErrorCode.USER_ALREADY_DELETED);
      case BANNED:
        throw new UserException(ErrorCode.USER_BANNED);
      case INCOMPLETE_PROFILE:
        throw new UserException(ErrorCode.USER_PROFILE_NOT_SET);
    }

    /*follow 도메인 생성*/
    Follow follow = Follow.createFollow(command.userId(), targetUser.getUserId());

    /*targetUser가 공개 개정일 경우 APPROVED 처리*/
    if (!targetUser.isPrivate()) {
      follow.markAsApproved();
    }

    /*저장*/
    followCommandPort.createFollow(follow);

    log.info("팔로우 요청 전송 : {}", command.targetUserId());

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
        .orElseThrow(() -> new FollowException(ErrorCode.FOLLOW_REQUEST_NOT_FOUND));

    /*내가 받은 팔로우 요청이 아닌 경우*/
    if (!follow.getFollowingId().equals(command.userId())) {
      throw new FollowException(ErrorCode.FOLLOW_REQUEST_FORBIDDEN);
    }

    /*팔로우 요청 수락 처리*/
    follow.markAsApproved();

    /*업데이트*/
    followCommandPort.updateFollowStatus(follow);
    log.info("팔로우 요청 수락 : {}", follow.getFollowerId());
  }

  @Transactional
  @Override
  public void rejectFollowRequest(HandleFollowRequestCommand command) {
    /*팔로우 요청 조회*/
    Follow follow = followQueryPort.findByIdAndStatusType(command.followId(),
            FollowStatusType.PENDING)
        .orElseThrow(() -> new FollowException(ErrorCode.FOLLOW_REQUEST_NOT_FOUND));

    /*내가 받은 팔로우 요청이 아닌 경우*/
    if (!follow.getFollowingId().equals(command.userId())) {
      throw new FollowException(ErrorCode.FOLLOW_REQUEST_FORBIDDEN);
    }

    /*삭제 처리*/
    followCommandPort.deleteFollow(command.followId());
    log.info("팔로우 요청 거절 : {}", follow.getFollowerId());
  }
}
