package com.threadly.post.comment.like.response;

/**
 * 게시글 댓글 좋아요 저장 응답 DTO
 */
public record CreatePostCommentLikeResponse(
    String commentId,
    boolean liked
) {

}
