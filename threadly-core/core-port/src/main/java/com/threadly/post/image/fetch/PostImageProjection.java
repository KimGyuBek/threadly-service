package com.threadly.post.image.fetch;

/**
 * 게시글 이미지 데이터 프로젝션 객체
 */
public interface PostImageProjection {

  String getImageUrl();
  int getImageOrder();

}
