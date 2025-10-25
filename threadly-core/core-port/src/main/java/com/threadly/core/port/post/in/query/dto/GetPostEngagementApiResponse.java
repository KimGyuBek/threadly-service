package com.threadly.core.port.post.in.query.dto;

/**
 * 게시글 활동 조회 API 응답 DTO
 */
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
