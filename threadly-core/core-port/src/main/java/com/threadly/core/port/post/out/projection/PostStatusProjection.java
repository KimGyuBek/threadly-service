package com.threadly.core.port.post.out.projection;

/**
 * 게시글 좋아요/댓글 수 DTO
 */
public interface PostStatusProjection {

  int getLikeCount();
  int getCommentCount();


}
