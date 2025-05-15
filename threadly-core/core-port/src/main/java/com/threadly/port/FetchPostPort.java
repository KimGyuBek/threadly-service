package com.threadly.port;

import com.threadly.posts.Post;
import java.util.Optional;

/**
 * 게시글 조회 관련 port
 */
public interface FetchPostPort {

  /**
   * postId로 post 조회
   * @param postId
   * @return
   */
  Optional<Post> findById(String postId);

}
