package com.threadly.post.comment.get;

import java.time.LocalDateTime;

/**
 * 게시글 댓글 커서 기반 목록 조회 요청 쿼리 객체
 */
public record GetPostCommentListQuery(
    String postId,
    String userId,
    LocalDateTime cursorCommentedAt,
    String cursorCommentId,
    int limit
) {

}
