package com.threadly.post;

import com.threadly.posts.Post;

/**
 * 게시글 생성 관련 port
 */
public interface CreatePostPort {

  /**
   * post 저장
   * @param post
   * @return
   */
  Post savePost(Post post);



}
