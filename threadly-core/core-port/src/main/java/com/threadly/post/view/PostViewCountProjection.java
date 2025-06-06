package com.threadly.post.view;

import lombok.Getter;

/**
 * 게시글 조회 수 조회 프로젝션 객체
 */
public interface PostViewCountProjection {

  String getPostId();

  long getViewCount();


}
