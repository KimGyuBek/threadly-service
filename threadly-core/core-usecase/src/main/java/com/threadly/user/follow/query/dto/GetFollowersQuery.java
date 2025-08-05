package com.threadly.user.follow.query.dto;

import java.time.LocalDateTime;

/**
 * 커서 기반 팔로워 목록 조회 쿼리 객체
 */
public record GetFollowersQuery(
    String userId,
    String targetUserId,
    LocalDateTime cursorFollowedAt,
    String cursorFollowerId,
    int limit
) {

}
