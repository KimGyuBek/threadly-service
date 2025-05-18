package com.threadly.controller.post.request;

/**
 * 게시글 댓글 생성 요청 DTO
 * @param content
 */
public record CreatePostCommentRequest(
    String content

) {

}
