package com.threadly.post.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 게시글 작성 요청 DTO
 */

public record CreatePostRequest(@NotBlank @NotNull @Size(max = 1000) String content,
                                @NotNull List<ImageRequest> images) {


  /**
   * 이미지 요청 DTO
   *
   * @param imageId
   * @param imageOrder
   */
  public record ImageRequest(@NotNull @NotBlank String imageId, @Min(0) int imageOrder) {

  }

}
