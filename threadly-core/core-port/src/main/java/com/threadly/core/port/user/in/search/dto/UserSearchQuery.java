package com.threadly.core.port.user.in.search.dto;

/**
 * 사용자 검색 쿼리 DTO
 */
public record UserSearchQuery(
    String userId,
    String keyword,
    String cursorNickname,
    int limit
) {


}
