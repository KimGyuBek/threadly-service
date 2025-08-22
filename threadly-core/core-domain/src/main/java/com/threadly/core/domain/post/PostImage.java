package com.threadly.core.domain.post;

import com.threadly.core.domain.image.ImageStatus;
import com.threadly.commons.utils.RandomUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 게시글 이미지 도메인
 */
@Getter
@AllArgsConstructor
public class PostImage {

  private String postImageId;
  private String storedName;
  private String imageUrl;
  private int imageOrder = 0;
  private ImageStatus status;

  /**
   * 새로운 postimage 도메인 생성
   *
   * @param storedName
   * @param imageUrl
   * @return
   */
  public static PostImage newPostImage(String storedName,
      String imageUrl) {
    return new PostImage(
        RandomUtils.generateNanoId(),
        storedName,
        imageUrl,
        -1,
        ImageStatus.TEMPORARY
    );
  }


  @Override
  public String toString() {
    return "PostImage{" +
        "postImageId='" + postImageId + '\'' +
//        ", postId='" + postId + '\'' +
        ", storedName='" + storedName +'\'' +
        ", imageUrl='" + imageUrl + '\'' +
        ", imageOrder=" + imageOrder +
        '}';
  }


}
