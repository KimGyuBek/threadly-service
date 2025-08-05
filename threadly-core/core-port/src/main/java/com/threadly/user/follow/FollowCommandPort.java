package com.threadly.user.follow;

import com.threadly.follow.Follow;

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
   * @param follow
   */
  void updateFollowStatus(Follow follow);

  /**
   * 주어진 followId에 해당하는 follow 삭제
   * @param followId
   */
  void deleteFollow(String followId);



}
