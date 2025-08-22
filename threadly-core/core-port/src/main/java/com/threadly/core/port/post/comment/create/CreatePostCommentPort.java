package com.threadly.core.port.post.comment.create;

import com.threadly.core.domain.post.comment.PostComment;

/**
 * 게시글 댓글 저장 관련 port
 */
public interface CreatePostCommentPort {

  /**
   * 게시글 댓글 저장
   * @param postComment
   * @return
   */
  void savePostComment(
      PostComment postComment);


}
