package com.threadly.core.port.post.in.comment.create;

import java.time.LocalDateTime;

/**
 * 게시글 댓글 생성 응답 API DTO
 */
public record CreatePostCommentApiResponse(
    String commentId,
    String userId,
    String userNickname,
    String userProfileImageUrl,
    String content,
    LocalDateTime createdAt
) {


}
