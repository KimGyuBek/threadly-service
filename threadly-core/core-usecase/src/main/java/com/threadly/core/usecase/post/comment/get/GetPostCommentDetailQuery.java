package com.threadly.core.usecase.post.comment.get;

/**
 * 게시글 댓글 조회 요청 쿼리 객체
 */
public record GetPostCommentDetailQuery(
    String commentId,
    String userId
) {

}
