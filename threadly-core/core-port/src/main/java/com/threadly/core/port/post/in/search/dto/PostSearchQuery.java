package com.threadly.core.port.post.in.search.dto;

import java.time.LocalDateTime;

/**
 * 게시글 검색 쿼리 DTO
 */
public record PostSearchQuery(
    String userId,
    String keyword,
    PostSearchSortType sortType,
    String cursorPostId,
    LocalDateTime cursorPostedAt,
    int limit
) {


}
