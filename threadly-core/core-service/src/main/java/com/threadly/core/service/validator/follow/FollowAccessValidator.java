package com.threadly.core.service.validator.follow;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.user.UserException;
import com.threadly.core.domain.follow.FollowStatusType;
import com.threadly.core.port.user.out.FetchUserPort;
import com.threadly.core.port.follow.out.FollowQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


/**
 * 대상 사용자의 프로필 접근 가능 여부 검증
 */
@Component
@RequiredArgsConstructor
public class FollowAccessValidator {

  private final FollowQueryPort followQueryPort;
  private final FetchUserPort fetchUserPort;


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
  public FollowStatusType validateProfileAccessibleWithException(String userId,
      String targetUserId) {
    if (userId.equals(targetUserId)) {
      return FollowStatusType.SELF;
    }

    FollowStatusType followStatusType = followQueryPort.findFollowStatusType(userId, targetUserId)
        .orElse(FollowStatusType.NONE);

    if (isPrivateUser(targetUserId) && !followStatusType.equals(
        FollowStatusType.APPROVED)) {
      throw new UserException(ErrorCode.USER_PROFILE_PRIVATE);
    }

    return followStatusType;
  }

  public FollowStatusType validateProfileAccessible(String userId, String targetUserId) {
    if (userId.equals(targetUserId)) {
      return FollowStatusType.SELF;
    }

    return followQueryPort.findFollowStatusType(userId, targetUserId)
        .orElse(FollowStatusType.NONE);
  }

  /**
   * 주어진 targetUserId에 해당하는 사용자의 계정 공개유무 조회
   *
   * @param targetUserId
   * @return
   */
  private boolean isPrivateUser(String targetUserId) {
    return fetchUserPort.isUserPrivate(targetUserId);
  }

}
