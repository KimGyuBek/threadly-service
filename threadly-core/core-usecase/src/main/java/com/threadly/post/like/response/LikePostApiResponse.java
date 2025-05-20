package com.threadly.post.like.response;

/**
 * 게시글 좋아요 요청 API 응답
 */
public record LikePostApiResponse(
    String postId,
    long likeCount
) {


}
