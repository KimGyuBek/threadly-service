package com.threadly.post;

import com.threadly.util.RandomUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * 게시글 이미지 도메인
 */
@Getter
@AllArgsConstructor
@Builder
public class PostImage {

  private String postImageId;
  private String postId;
  private String storedName;
  private String imageUrl;
  private int imageOrder = 0;

  /**
   * 새로운 postimage 도메인 생성
   *
   * @param storedName
   * @param imageUrl
   * @param imageOrder
   * @return
   */
  public static PostImage newPostImage(String postId, String storedName,
      String imageUrl, int imageOrder) {
    return new PostImage(
        RandomUtils.generateNanoId(),
        postId,
        storedName,
        imageUrl,
        imageOrder
    );
  }

  @Override
  public String toString() {
    return "PostImage{" +
        "postImageId='" + postImageId + '\'' +
        ", postId='" + postId + '\'' +
        ", storedName='" + storedName + '\'' +
        ", imageUrl='" + imageUrl + '\'' +
        ", imageOrder=" + imageOrder +
        '}';
  }
}
