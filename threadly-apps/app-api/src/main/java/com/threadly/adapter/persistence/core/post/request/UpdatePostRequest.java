package com.threadly.adapter.persistence.core.post.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 게시글 수정 요청 DTO
 */

public record UpdatePostRequest(@NotBlank @NotNull @Size(max = 1000) String content) {


}
