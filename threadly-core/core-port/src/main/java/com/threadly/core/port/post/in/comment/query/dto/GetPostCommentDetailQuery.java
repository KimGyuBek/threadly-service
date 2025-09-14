package com.threadly.core.port.post.in.comment.query.dto;

/**
 * 게시글 댓글 조회 요청 쿼리 객체
 */
public record GetPostCommentDetailQuery(
    String commentId,
    String userId
) {

}
