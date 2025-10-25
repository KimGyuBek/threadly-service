package com.threadly.core.port.post.out.sesarch;

import java.time.LocalDateTime;

public interface PostSearchProjection {

  /*Post Details*/
  String getPostId();
  String getContent();
  long getLikeCount();
  long getCommentCount();
  boolean isLiked();
  long getViewCount();
  LocalDateTime getPostedAt();

  /*UserPreview*/
  String getUserId();
  String getUserNickname();
  String getUserProfileImageUrl();
}
