package com.threadly.core.port.post.save;

import com.threadly.core.domain.post.Post;

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
