package com.threadly.core.port.post.in.like.comment.command.dto;

/**
 * 댓글 좋아요 API 응답
 */
public record LikePostCommentApiResponse(
    String commentId,
    long likeCount
) {

}
