package com.threadly.follow.command;

import com.threadly.follow.command.dto.FollowUserApiResponse;
import com.threadly.follow.command.dto.FollowUserCommand;
import com.threadly.follow.command.dto.HandleFollowRequestCommand;

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
   * @param command
   */
  void approveFollowRequest(HandleFollowRequestCommand  command);

  /**
   * 주어진 followId에 해당하는 팔로우 요청 거절
   * @param command
   */
  void rejectFollowRequest(HandleFollowRequestCommand  command);


}
