package com.threadly.post.comment.like;

import com.threadly.posts.comment.CommentLike;

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

}
