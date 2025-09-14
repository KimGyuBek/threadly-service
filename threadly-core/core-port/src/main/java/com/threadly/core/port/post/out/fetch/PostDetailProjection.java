package com.threadly.core.port.post.out.fetch;

import com.threadly.core.domain.post.PostStatus;
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