package com.threadly.user.follow;

import com.threadly.user.FollowStatusType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 팔로우 조회 관련 port
 */
public interface FollowQueryPort {

  /**
   * 주어진 userId에 해당하는 사용자와 followingId에 해당하는 사용자를 팔로우 하는지 검증
   *
   * @param followerId
   * @param followingId
   * @return
   */
  boolean isFollowing(String followerId, String followingId);

  /**
   * 주어진 followerId, followingId에 해당하는 FollowStatusType 조회
   *
   * @param followerId
   * @param followingId
   * @return
   */
  Optional<FollowStatusType> findFollowStatusType(String followerId, String followingId);

  /**
   * 주어진 userId에 해당하는 팔로우 요청 목록 커서 기반 조회
   *
   * @param userId
   * @param cursorFollowRequestedAt
   * @param cursorFollowId
   * @param limit
   * @return
   */
  List<FollowRequestsProjection> findFollowRequestsByCursor(String userId,
      LocalDateTime cursorFollowRequestedAt, String cursorFollowId, int limit);

  /**
   * 주어진 targetUserId에 해당하는 사용자의 팔로워 목록 커서 기반 조회
   * @param targetUserId
   * @param cursorFollowedAt
   * @param cursorFollowerId
   * @param limit
   * @return
   */
  List<FollowerProjection> findFollowersByCursor(String targetUserId,
      LocalDateTime cursorFollowedAt, String cursorFollowerId, int limit);
}
