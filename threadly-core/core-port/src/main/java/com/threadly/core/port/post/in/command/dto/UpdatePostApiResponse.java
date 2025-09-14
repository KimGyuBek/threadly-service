package com.threadly.core.port.post.in.command.dto;

import java.time.LocalDateTime;

/**
 * 게시글 수정 API 응답 DTO
 */
public record UpdatePostApiResponse(
    String postId,
    String userId,
    String userProfileImageUrl,
    String userNickname,
    String content,
    long viewCount,
    LocalDateTime postedAt,
    long likeCount,
    long commentCount,
    boolean liked

) {

}
