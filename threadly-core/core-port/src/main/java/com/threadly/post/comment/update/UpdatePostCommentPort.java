package com.threadly.post.comment.update;

import com.threadly.posts.PostCommentStatusType;

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
  void updatePostCommentStatus(String commentId, PostCommentStatusType status);

}
