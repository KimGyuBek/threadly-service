package com.threadly.post.response;

/**
 * 게시글 좋아요/댓글 수 DTO
 */
public interface PostStatusResponse {

  int getLikeCount();
  int getCommentCount();


}
