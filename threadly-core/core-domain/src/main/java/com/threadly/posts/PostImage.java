package com.threadly.posts;

import lombok.Getter;

/**
 * 게시글 이미지 도메인
 */
@Getter
public class PostImage {

  private String postImageId;
  private String postId;
  private String userId;
  private String storedFileName;
  private String url;

}
