package com.threadly.core.port.post.out.like.comment;

/**
 * 게시글 댓글 좋아요 삭제 관련 Port
 */
public interface DeletePostCommentLikePort {

  /**
   * 댓글 좋아요 삭제
   * @param commentId
   * @param userId
   *
   */
  void deletePostCommentLike(String commentId, String userId);


  /**
   * postId에 해당하는 댓글들의 좋아요 목록 전체 삭제
   * @param postId
   */
  void deleteAllByPostId(String postId);

}
