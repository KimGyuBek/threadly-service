package com.threadly.core.service.follow.validator;

import com.google.common.base.Objects;
import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.follow.FollowException;
import com.threadly.commons.exception.user.UserException;
import com.threadly.core.domain.follow.Follow;
import com.threadly.core.domain.follow.FollowStatus;
import com.threadly.core.port.follow.out.FollowQueryPort;
import com.threadly.core.port.user.out.UserQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * 대상 사용자의 프로필 접근 가능 여부 검증
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FollowValidator {

  private final FollowQueryPort followQueryPort;
  private final UserQueryPort userQueryPort;


  /**
   * 주어진 followId에 해당하는 팔로우 요청 대기 조회
   *
   * @param followId
   * @return
   */
  public Follow getPendingFollowOrThrow(String followId) {
    return followQueryPort.findByIdAndStatusType(followId, FollowStatus.PENDING)
        .orElseThrow(() -> new FollowException(ErrorCode.FOLLOW_RELATION_NOT_FOUND));
  }

  /**
   * 주어진 followingId와 userId가 일치하는지 검증
   *
   * @param followingId
   * @param userId
   */
  public void validateFollowRequestReceiver(String followingId, String userId) {
    if (!followingId.equals(userId)) {
      log.warn("followingId와 userId가 일치하지 않음, followingId: {}, userId: {}", followingId, userId);

      throw new FollowException(ErrorCode.FOLLOW_REQUEST_FORBIDDEN);
    }
  }

  /**
   * 주어진 targetUserId에 해당하는 대상에 대해 사용자가 접근이 가능핮니 검증
   * <p>
   * 대상 사용자가 비공개 계정인 경우, 요청자가 본인이거나 팔로우 승인 상태가 아닐 경우 접근을 차단
   * </p>
   *
   * @param userId
   * @param targetUserId
   * @return followStatusType
   * @throws UserException
   */
  public FollowStatus validateProfileAccessibleWithException(String userId,
      String targetUserId) {
    if (userId.equals(targetUserId)) {
      return FollowStatus.SELF;
    }

    FollowStatus followStatus = followQueryPort.findFollowStatusType(userId, targetUserId)
        .orElse(FollowStatus.NONE);

    if (isPrivateUser(targetUserId) && !followStatus.equals(
        FollowStatus.APPROVED)) {
      throw new UserException(ErrorCode.USER_PROFILE_PRIVATE);
    }

    return followStatus;
  }

  /**
   * 주어진 userId의 사용자가 targetUserId의 사용자 프로필에 접근 가능한지 검증
   *
   * @param userId
   * @param targetUserId
   * @return
   */
  public FollowStatus validateProfileAccessible(String userId, String targetUserId) {
    if (userId.equals(targetUserId)) {
      log.debug("userId와 targetUserId가 동일함, userId={}, targetUserId={}", userId, targetUserId);

      return FollowStatus.SELF;
    }

    log.debug("접근 가능한 팔로우 상태, followUserId={}, targetUserId={}", userId, targetUserId);
    return followQueryPort.findFollowStatusType(userId, targetUserId)
        .orElse(FollowStatus.NONE);
  }

  /**
   * 주어진 targetUserId에 해당하는 사용자의 계정 공개유무 조회
   *
   * @param targetUserId
   * @return
   */
  private boolean isPrivateUser(String targetUserId) {
    return userQueryPort.isUserPrivate(targetUserId);
  }


  /**
   * 주어진 userId가 자신을 팔로우 하려는지 검증
   *
   * @param userId
   * @param targetUserId
   */
  public void validateNotSelfFollow(String userId, String targetUserId) {
    if (Objects.equal(userId, targetUserId)) {
      log.warn("Self follow 불가, userId={}, targetUserId={}", userId, targetUserId);

      throw new FollowException(ErrorCode.SELF_FOLLOW_REQUEST_NOT_ALLOWED);
    }
  }

  /**
   * 주어진 commnad에 해당하는 팔로우 조회 후 존재 하지 않을 경우 예외 발생
   *
   * @param followerId
   * @param followingId
   * @param followStatus
   * @throws FollowException
   */
  public void validateFollowExists(String followerId, String followingId,
      FollowStatus followStatus) {
    /*팔로우 요청 조회*/
    boolean exists = followQueryPort.existsByFollowerIdAndFollowingIdAndStatusType(followerId,
        followingId, followStatus);

    /*팔로우 요청이 존재하지 않는 경우*/
    if (!exists) {
      throw new FollowException(ErrorCode.FOLLOW_RELATION_NOT_FOUND);
    }
  }


}
