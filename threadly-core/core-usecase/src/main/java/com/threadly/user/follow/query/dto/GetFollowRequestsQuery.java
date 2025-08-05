package com.threadly.user.follow.query.dto;

import java.time.LocalDateTime;

/**
 * 커서 기반 팔로우 요청 목록 조회 쿼리 객체
 */
public record GetFollowRequestsQuery(
    String userId,
    LocalDateTime cursorFollowRequestedAt,
    String cursorFollowId,
    int limit
) {

}
