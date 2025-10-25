package com.threadly.core.port.post.in.comment.command.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * 게시글 댓글 생성 응답 API DTO
 */
@Schema(description = "게시글 댓글 생성 응답")
public record CreatePostCommentApiResponse(
    String commentId,
    String userId,
    String userNickname,
    String userProfileImageUrl,
    String content,
    LocalDateTime createdAt
) {


}
