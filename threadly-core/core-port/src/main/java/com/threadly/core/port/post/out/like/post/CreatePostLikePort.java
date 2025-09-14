package com.threadly.core.port.post.out.like.post;

import com.threadly.core.domain.post.PostLike;

/**
 * 게시글 좋아요 저장 관련 Port
 */
public interface CreatePostLikePort {

  /**
   * 게시글 좋아요 저장
   * @param postLike
   */
  void createPostLike(PostLike postLike);

}
