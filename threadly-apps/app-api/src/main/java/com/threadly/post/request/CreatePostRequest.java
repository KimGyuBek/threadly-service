package com.threadly.post.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 게시글 작성 요청 DTO
 */

public record CreatePostRequest(@NotBlank @NotNull String content) {


}
