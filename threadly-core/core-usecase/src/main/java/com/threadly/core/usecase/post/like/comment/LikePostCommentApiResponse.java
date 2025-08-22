package com.threadly.core.usecase.post.like.comment;

/**
 * 댓글 좋아요 API 응답
 */
public record LikePostCommentApiResponse(
    String commentId,
    long likeCount
) {

}
