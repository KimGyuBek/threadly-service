package com.threadly.core.port.follow.out;

import com.threadly.core.domain.follow.Follow;
import com.threadly.core.domain.follow.FollowStatus;

/**
 * 팔로우 command port
 */
public interface FollowCommandPort {

  /**
   * 팔로우 요청
   *
   * @param follow
   */
  void createFollow(Follow follow);

  /**
   * 팔로우 상태 변경
   *
   * @param follow
   */
  void updateFollowStatus(Follow follow);

  /**
   * 주어진 followId에 해당하는 follow 삭제
   *
   * @param followId
   */
  void deleteFollow(String followId);

  /**
   * 주어진 파라미터에 해당하는 팔로우 삭제
   * @param followerId
   * @param followingId
   * @param followStatus
   */
  void deleteByFollowerIdAndFollowingIdAndStatusType(String followerId, String followingId,
      FollowStatus followStatus);


}
