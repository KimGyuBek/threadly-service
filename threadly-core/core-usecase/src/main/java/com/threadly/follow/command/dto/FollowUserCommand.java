package com.threadly.follow.command.dto;

/**
 * 사용자 팔로우 요청 Command 객체
 * @param userId
 * @param targetUserId
 */
public record FollowUserCommand(
    String userId,
    String targetUserId
) {

}
