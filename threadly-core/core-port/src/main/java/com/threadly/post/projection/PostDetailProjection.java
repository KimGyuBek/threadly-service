package com.threadly.post.projection;

import com.threadly.posts.PostStatusType;
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

  int getViewCount();

  LocalDateTime getPostedAt();

  PostStatusType getPostStatus();

  long getLikeCount();

  long getCommentCount();

  boolean isLiked();
}
