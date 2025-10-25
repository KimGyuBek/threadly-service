package com.threadly.core.port.post.out.image.projection;

/**
 * 게시글 이미지 데이터 프로젝션 객체
 */
public interface PostImageProjection {

  String getPostId();
  String getImageId();
  String getImageUrl();
  int getImageOrder();

}
