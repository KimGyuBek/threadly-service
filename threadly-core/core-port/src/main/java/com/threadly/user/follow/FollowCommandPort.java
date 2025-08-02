package com.threadly.user.follow;

import com.threadly.user.Follow;

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
   * 팔로우 승인
   * @param followId
   */
  void approveFollow(String followId);

  /**
   * 팔로우 거절
   * @param followId
   */
  void rejectFollow(String followId);


}
