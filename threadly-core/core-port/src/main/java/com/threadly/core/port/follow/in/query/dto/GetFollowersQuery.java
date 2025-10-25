package com.threadly.core.port.follow.in.query.dto;

import java.time.LocalDateTime;

/**
 * 커서 기반 팔로워 목록 조회 쿼리 객체
 */
public record GetFollowersQuery(
    String userId,
    String targetUserId,
    LocalDateTime cursorTimestamp,
    String cursorId,
    int limit
) {

}
