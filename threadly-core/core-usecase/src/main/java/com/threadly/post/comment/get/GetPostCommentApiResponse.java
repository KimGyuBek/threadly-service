package com.threadly.post.comment.get;

import java.time.LocalDateTime;

/**
 * 댓글 조회 API 응답 객체
 */
public record GetPostCommentApiResponse(
    String postId,
    String commentId,
    String commenterId,
    String commenterNickname,
    String commenterProfileImageUrl,
    LocalDateTime commentedAt,
    long likeCount,
    String content,
    boolean liked
) {


}
