package com.threadly.core.usecase.follow.command;

import com.threadly.core.usecase.follow.command.dto.FollowRelationCommand;
import com.threadly.core.usecase.follow.command.dto.FollowUserApiResponse;
import com.threadly.core.usecase.follow.command.dto.FollowUserCommand;
import com.threadly.core.usecase.follow.command.dto.HandleFollowRequestCommand;

/**
 * 사용자 팔로우 관련 command usecase
 */
public interface FollowCommandUseCase {

  /**
   * 사용자 팔로우 처리
   *
   * @param command
   * @return
   */
  FollowUserApiResponse followUser(FollowUserCommand command);

  /**
   * 주어진 followId에 해당하는 팔로우 요청 수락
   *
   * @param command
   */
  void approveFollowRequest(HandleFollowRequestCommand command);

  /**
   * 주어진 followId에 해당하는 팔로우 요청 거절
   *
   * @param command
   */
  void rejectFollowRequest(HandleFollowRequestCommand command);

  /**
   * 주어진 targetUserId에 해당하는 사용자에 대한 팔로우 요청 취소
   *
   * @param command
   */
  void cancelFollowRequest(FollowRelationCommand command);

  /**
   * 주어진 targetUserId에 해당하는 사용자 언팔로우
   *
   * @param command
   */
  void unfollowUser(FollowRelationCommand command);

  /**
   * 주어진 targetUserId에 해당하는 사용자 팔로워 삭제
   *
   * @param command
   */
  void removeFollower(FollowRelationCommand command);

}
