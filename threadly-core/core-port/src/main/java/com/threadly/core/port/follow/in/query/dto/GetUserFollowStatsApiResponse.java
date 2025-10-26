package com.threadly.core.port.follow.in.query.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 사용자 팔로잉, 팔로워 수 조회 응답 API
 */
@Schema(description = "팔로우 통계 조회 응답")
public record GetUserFollowStatsApiResponse(
    int followerCount,
    int followingCount
) {

}
