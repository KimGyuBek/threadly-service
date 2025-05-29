package com.threadly.post.like.comment;


import java.time.LocalDateTime;

/**
 * 게시글 댓글 좋아요 커서 기반 조회 요청 쿼리 객체
 *
 * @param postId
 * @param commentId
 * @param userId
 * @param cursorLikedAt
 * @param cursorLikerId
 * @param limit
 */
public record GetPostCommentLikersQuery(
    String postId,
    String commentId,
    LocalDateTime cursorLikedAt,
    String cursorLikerId,
    int limit
) {

}
