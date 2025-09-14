package com.threadly.core.port.post.in.image;

import java.util.List;

/**
 * 게시글 이미지 업로드 api 응답 객체
 */
public record UploadPostImagesApiResponse(
    List<PostImageResponse> images
) {

  public record PostImageResponse(
      String imageId,
      String imageUrl
  ) {

  }
}
