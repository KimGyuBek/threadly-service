package com.threadly.post.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 게시글 댓글 생성 요청 DTO
 *
 * @param content
 */
public record CreatePostCommentRequest(
    @NotNull
    @NotBlank
    @Size(max = 255)
    String content
) {

}
