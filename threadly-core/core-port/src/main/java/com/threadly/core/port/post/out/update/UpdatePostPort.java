package com.threadly.core.port.post.out.update;

import com.threadly.core.domain.post.Post;

/**
 * 게시글 수정 관련 port
 */
public interface UpdatePostPort {

  /**
   * post 업데이트
   * @param post
   */
  void updatePost(Post post);

  /**
   * 게시글 상태 변경
   *
   * @param post
   */
  void changeStatus(Post post);

  /**
   * 게시글 조회 수 증가
   * @param postId
   */
  void increaseViewCount(String postId);


}
