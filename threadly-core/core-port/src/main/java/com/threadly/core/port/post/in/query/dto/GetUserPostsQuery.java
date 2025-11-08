package com.threadly.core.port.post.in.query.dto;

import java.time.LocalDateTime;

/**
 * 특정 사용자가 작성한 게시글 목록 조회 Query
 */
public record GetUserPostsQuery(
    String userId,
    String targetId,
    LocalDateTime cursorPostedAt,
    String cursorPostId,
    int limit
) {

}
