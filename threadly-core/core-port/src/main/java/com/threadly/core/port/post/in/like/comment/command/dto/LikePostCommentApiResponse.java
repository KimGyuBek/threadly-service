package com.threadly.core.port.post.in.like.comment.command.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 댓글 좋아요 API 응답
 */
@Schema(description = "댓글 좋아요 응답")
public record LikePostCommentApiResponse(
    String commentId,
    long likeCount
) {

}
