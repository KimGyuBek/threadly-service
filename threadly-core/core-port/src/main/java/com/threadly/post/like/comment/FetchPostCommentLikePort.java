package com.threadly.post.like.comment;

/**
 * 게시글 댓글 좋아요 조회 관련 port
 */
public interface FetchPostCommentLikePort {

  /**
   * 사용자가 좋아요를 눌렀는지 조회
   * @param commentId
   * @param userId
   * @return
   */
  boolean existsByCommentIdAndUserId(String commentId, String userId);

  /**
   * commentId에 해당하는 좋아요 수 조회
   * @param commentId
   * @return
   */
  long fetchLikeCountByCommentId(String commentId);



}
