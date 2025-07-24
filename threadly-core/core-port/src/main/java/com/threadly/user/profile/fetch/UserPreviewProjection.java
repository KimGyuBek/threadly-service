package com.threadly.user.profile.fetch;

/**
 * 사용자 댓글 프리뷰 조회 프로젝션 객체
 */
public interface UserPreviewProjection {
  String getNickname();
  String getProfileImageUrl();
}
