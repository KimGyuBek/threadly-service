package com.threadly.user.request.follow;

import com.threadly.user.follow.FollowUserCommand;

/**
 * 사용자 팔로우 요청 객체
 */
public record FollowRequest(
    String targetUserId
) {

  /**
   * request -> command
   *
   * @param userId
   * @return
   */
  public FollowUserCommand toCommand(String userId) {
    return new FollowUserCommand(userId, this.targetUserId);

  }

}
