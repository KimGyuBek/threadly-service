package com.threadly.post.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 게시글 수정 요청 DTO
 */

public record UpdatePostRequest(@NotBlank @NotNull String content) {


}
