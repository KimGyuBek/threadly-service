package com.threadly.post.response;

import java.time.LocalDateTime;

/**
 * 게시글 상세 정보 DTO
 */
public interface PostDetailResponse {
  String getPostId();
  String getUserId();
  String getUserNickname();
  String getUserProfileImageUrl();
  String getContent();
  int getViewCount();
  LocalDateTime getPostedAt();
}
