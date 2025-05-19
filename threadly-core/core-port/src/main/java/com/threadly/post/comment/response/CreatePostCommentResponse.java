package com.threadly.post.comment.response;

import java.time.LocalDateTime;

/**
 * 댓글 저장 응답 DTO
 * @param commentId
 * @param userId
 * @param content
 * @param createdAt
 */
public record CreatePostCommentResponse(
    String commentId,
    String userId,
    String content,
    LocalDateTime createdAt
) {

}
