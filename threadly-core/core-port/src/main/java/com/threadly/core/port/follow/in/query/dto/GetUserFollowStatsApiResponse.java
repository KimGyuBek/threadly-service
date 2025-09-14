package com.threadly.core.port.follow.in.query.dto;


/**
 * 사용자 팔로잉, 팔로워 수 조회 응답 API
 */
public record GetUserFollowStatsApiResponse(
    int followerCount,
    int followingCount
) {

}
