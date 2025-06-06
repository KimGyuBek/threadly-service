package com.threadly.post.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 게시글 작성 요청 DTO
 */

public record CreatePostRequest(@NotBlank @NotNull @Size(max = 1000) String content) {


}
