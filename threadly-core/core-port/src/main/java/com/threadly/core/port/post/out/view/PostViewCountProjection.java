package com.threadly.core.port.post.out.view;

/**
 * 게시글 조회 수 조회 프로젝션 객체
 */
public interface PostViewCountProjection {

  String getPostId();

  long getViewCount();


}
