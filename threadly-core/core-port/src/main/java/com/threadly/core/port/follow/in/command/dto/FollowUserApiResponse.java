package com.threadly.core.port.follow.in.command.dto;

import com.threadly.core.domain.follow.FollowStatus;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 샤용자 팔로우 요청 API 응답 객체
 */
@Schema(description = "팔로우 응답")
public record FollowUserApiResponse(
    FollowStatus followStatus
) {

}
