package com.threadly.post.fetch;

import com.threadly.post.PostStatus;
import java.time.LocalDateTime;

/**
 * 게시글 상세 정보 DTO
 */
public interface PostDetailProjection {

  String getPostId();

  String getUserId();

  String getUserNickname();

  String getUserProfileImageUrl();

  String getContent();

  long getViewCount();

  LocalDateTime getPostedAt();

  PostStatus getPostStatus();

  long getLikeCount();

  long getCommentCount();

  boolean isLiked();
}