package com.threadly.user.follow;

import com.threadly.user.FollowStatusType;
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
   * @param followerId
   * @param followingId
   * @return
   */
  Optional<FollowStatusType> findFollowStatusType(String followerId, String followingId);

}
