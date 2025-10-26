package com.threadly.core.port.post.in.query.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 게시글 활동 조회 API 응답 DTO
 */
@Schema(description = "게시글 활동 조회 응답")
public record GetPostEngagementApiResponse(
    String postId,
    String authorId,
    String authorNickname,
    String authorProfileImageUrl,
    String content,
    long likeCount,
    boolean liked
) {

}
