package com.threadly.post.image.upload;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 이미지 업로드 응답 객체
 */
@Getter
@AllArgsConstructor
public class UploadImageResponse {

  private String storedName;
  private String imageUrl;
  private int imageOrder;

  @Override
  public String toString() {
    return "UploadImageResponse{" +
        "storedName='" + storedName + '\'' +
        ", imageUrl='" + imageUrl + '\'' +
        ", imageOrder=" + imageOrder +
        '}';
  }
}
