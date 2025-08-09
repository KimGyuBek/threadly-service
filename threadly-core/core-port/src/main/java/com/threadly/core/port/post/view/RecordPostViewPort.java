package com.threadly.core.port.post.view;

import java.time.Duration;

/**
 * 게시글 조회 기록 저장 port
 */
public interface RecordPostViewPort {


  /**
   * 사용자의 게시글 조회 유무를 redis에 저장
   *
   * @param postId
   * @param userId
   */
  void recordPostView(String postId, String userId, Duration ttl);

  boolean existsPostView(String postI, String userId);
}
