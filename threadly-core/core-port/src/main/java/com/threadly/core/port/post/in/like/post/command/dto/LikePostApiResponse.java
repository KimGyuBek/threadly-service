package com.threadly.core.port.post.in.like.post.command.dto;

/**
 * 게시글 좋아요 요청 API 응답
 */
public record LikePostApiResponse(
    String postId,
    long likeCount
) {


}
