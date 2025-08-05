package com.threadly.validator.follow;

import com.threadly.exception.ErrorCode;
import com.threadly.exception.user.UserException;
import com.threadly.user.FollowStatusType;
import com.threadly.user.follow.FollowQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


/**
 * 대상 사용자의 프로필 접근 가능 여부 검증
 */
@Component
@RequiredArgsConstructor
public class FollowAccessValidator {

  private final FollowQueryPort followQueryPort;


  /**
   * 주어진 targetUserId에 해당하는 대상에 대해 사용자가 접근이 가능핮니 검증
   * <p>
   * 대상 사용자가 비공개 계정인 경우, 요청자가 본인이거나 팔로우 승인 상태가 아닐 경우 접근을 차단
   * </p>
   *
   * @param userId
   * @param targetUserId
   * @param isPrivate
   * @return followStatusType
   * @throws UserException
   */
  public FollowStatusType validateProfileAccessible(String userId, String targetUserId,
      boolean isPrivate) {
    FollowStatusType followStatusType = followQueryPort.findFollowStatusType(userId, targetUserId)
        .orElse(FollowStatusType.NONE);
    if (isPrivate && !followStatusType.equals(
        FollowStatusType.APPROVED)) {
      throw new UserException(ErrorCode.USER_PROFILE_PRIVATE);
    }

    return followStatusType;
  }

}
