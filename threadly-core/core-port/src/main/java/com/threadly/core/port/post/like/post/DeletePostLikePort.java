package com.threadly.core.port.post.like.post;

/**
 * 게시글 좋아요 삭제 관련 Port
 */
public interface DeletePostLikePort {

  /**
   * posId와 userId에 해당하는 좋아요 삭제
   * @param postId
   * @param userId
   * @return
   */
  int deleteByPostIdAndUserId(String postId, String userId);

  /**
   * 특정 게시글의 좋아요 전체 삭제
   * @param postId
   */
  void deleteAllByPostId(String postId);

}
