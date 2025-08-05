package com.threadly.user.follow.command;

import com.threadly.user.follow.command.dto.FollowUserApiResponse;
import com.threadly.user.follow.command.dto.FollowUserCommand;

/**
 * 사용자 팔로우 관련 usecase
 */
public interface FollowUserUseCase {

  /**
   * 사용자 팔로우 처리
   *
   * @param command
   * @return
   */
  FollowUserApiResponse followUser(FollowUserCommand command);

}
