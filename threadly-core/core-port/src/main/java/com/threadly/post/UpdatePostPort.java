package com.threadly.post;

import com.threadly.posts.Post;

/**
 * 게시글 수정 관련 port
 */
public interface UpdatePostPort {

  /**
   * post 업데이트
   * @param post
   */
  void updatePost(Post post);



}
