package com.threadly.follow.command.dto;

/**
 * 팔로우 관계 변경 command 객체
 */
public record FollowRelationCommand(
    String userId,
    String targetUserId
) {

}
