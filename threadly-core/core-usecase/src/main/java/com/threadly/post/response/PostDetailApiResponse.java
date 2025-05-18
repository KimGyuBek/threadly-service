package com.threadly.post.response;

import java.time.LocalDateTime;

/**
 * 게시글 조회 응답 APi DTO
 */
public record PostDetailApiResponse(
    String postId,
    String userId,
    String userProfileImageUrl,
    String userNickname,
    String content,
    int viewCount,
    LocalDateTime postedAt
) {


}
