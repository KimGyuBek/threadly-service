package com.threadly.post.save;

import com.threadly.posts.Post;

/**
 * 게시글 저장 관련 port
 */
public interface SavePostPort {

  /**
   * post 저장
   * @param post
   * @return
   */
  Post savePost(Post post);



}
