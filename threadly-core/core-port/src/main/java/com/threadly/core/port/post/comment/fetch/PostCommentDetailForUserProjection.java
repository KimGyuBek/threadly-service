package com.threadly.core.port.post.comment.fetch;

import java.time.LocalDateTime;

/**
 * 게시글 댓글 상세 정보 DB 조회를 위한 프로젝션 객체
 */
public interface PostCommentDetailForUserProjection {

  String getPostId();

  String getCommentId();

  String getCommenterId();

  String getCommenterNickname();

  String getCommenterProfileImageUrl();

  long getLikeCount();

  LocalDateTime getCommentedAt();

  String getContent();

  boolean isLiked();


}
