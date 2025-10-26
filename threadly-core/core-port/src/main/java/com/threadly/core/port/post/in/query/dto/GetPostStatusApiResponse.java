package com.threadly.core.port.post.in.query.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 게시글 좋아요/댓글 수 통계 API 응답 DTO
 */
@Schema(description = "게시글 통계 조회 응답")
public record GetPostStatusApiResponse(
    int likesCount,
    int commentCount
) {

}
