package com.threadly.core.usecase.follow.query.dto;

import java.time.LocalDateTime;

/**
 * 커서 기반 팔로우 요청 목록 조회 쿼리 객체
 */
public record GetFollowRequestsQuery(
    String userId,
    LocalDateTime cursorTimestamp,
    String cursorId,
    int limit
) {

}
