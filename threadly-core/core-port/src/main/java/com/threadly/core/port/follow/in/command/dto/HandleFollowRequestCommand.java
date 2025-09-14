package com.threadly.core.port.follow.in.command.dto;

/**
 * 팔로우 요청 수락 및 거절 요청 command 객체
 */
public record HandleFollowRequestCommand(
    String userId,
    String followId
) {

}
