package com.threadly.core.port.post.in.status;

/**
 * 게시글 좋아요/댓글 수 통계 API 응답 DTO
 */
public record GetPostStatusApiResponse(
    int likesCount,
    int commentCount
) {

}
