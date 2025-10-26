package com.threadly.core.port.post.in.image;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 게시글 이미지 업로드 api 응답 객체
 */
@Schema(description = "게시글 이미지 업로드 응답")
public record UploadPostImagesApiResponse(
    List<PostImageResponse> images
) {

  public record PostImageResponse(
      String imageId,
      String imageUrl
  ) {

  }
}
