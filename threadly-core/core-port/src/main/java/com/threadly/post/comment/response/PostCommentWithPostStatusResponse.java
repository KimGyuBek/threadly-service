package com.threadly.post.comment.response;

/**
 * 게시글 댓글, 게시글 상태 조회 응답 DTO
 */
public interface PostCommentWithPostStatusResponse {
  String getCommentId();
  String getPostId();
  String getUserId();
  String getContent();
  String getCommentStatus();
  String getPostStatus();
}
