package com.threadly.core.port.post.in.like.post.command.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 게시글 좋아요 요청 API 응답
 */
@Schema(description = "게시글 좋아요 응답")
public record LikePostApiResponse(
    String postId,
    long likeCount
) {


}
