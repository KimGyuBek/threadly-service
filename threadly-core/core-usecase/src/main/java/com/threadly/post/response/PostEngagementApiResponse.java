package com.threadly.post.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시글 활동 조회 API 응답 DTO
 */
public record PostEngagementApiResponse(
    String postId,
    String authorId,
    String authorNickname,
    String authorProfileImageUrl,
    String content,
    long likeCount,
    boolean liked
) {

}
