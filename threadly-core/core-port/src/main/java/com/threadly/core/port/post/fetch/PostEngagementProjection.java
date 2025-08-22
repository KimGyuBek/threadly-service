package com.threadly.core.port.post.fetch;

/**
 * 게시글 좋아요 관련 정보 조회 응답 객체
 */
public interface PostEngagementProjection {

  String getPostId();

  String getAuthorId();

  String getAuthorNickname();

  String getAuthorProfileImageUrl();

  String getContent();

  long getLikeCount();

  boolean isLiked();
}
