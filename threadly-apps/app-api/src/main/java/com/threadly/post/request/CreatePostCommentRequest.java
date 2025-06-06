package com.threadly.post.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 게시글 댓글 생성 요청 DTO
 *
 * @param content
 */
public record CreatePostCommentRequest(
    @NotNull
    @NotBlank
    String content
) {

}
