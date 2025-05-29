package com.threadly.post.like.post;

import com.threadly.posts.PostLike;

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
