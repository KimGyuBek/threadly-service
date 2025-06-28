package com.threadly.post.comment.update;

import com.threadly.post.PostCommentStatus;

/**
 * 게시글 댓글 업데이트 관련 Port
 */
public interface UpdatePostCommentPort {

  /**
   * 게시글 댓글 상태 변경
   *
   * @param postComment
   *
   */
  void updatePostCommentStatus(String commentId, PostCommentStatus status);

  /**
   * postId에 해당하는 댓글들 상태 변경
   * @param postId
   * @param status
   */
  void updateAllCommentStatusByPostId(String postId, PostCommentStatus status);

}
