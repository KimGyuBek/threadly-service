package com.threadly.post.response;

/**
 * 게시글 좋아요/댓글 수 통계 API 응답 DTO
 */
public record PostStatusApiResponse(
    int likesCount,
    int commentCount
) {

}
