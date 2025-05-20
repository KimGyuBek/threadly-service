package com.threadly.post.like;

/**
 * 게시글 좋아요 조회 관련  Port
 */
public interface FetchPostLikePort {

  /**
   * postId, userId에 행당하는 게시글 좋아요가 있는지 검증
   * @param postId
   * @param userId
   * @return
   */
  boolean existsByPostIdAndUserId(String postId, String userId);

  /**
   * postId에 해당하는 좋아요 수 조회
   * @param postId
   * @return
   */
  long getLikeCountByPostId(String postId);

}
