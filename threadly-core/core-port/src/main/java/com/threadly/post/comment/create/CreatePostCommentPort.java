package com.threadly.post.comment.create;

import com.threadly.post.comment.PostComment;

/**
 * 게시글 댓글 저장 관련 port
 */
public interface CreatePostCommentPort {

  /**
   * 게시글 댓글 저장
   * @param postComment
   * @return
   */
  CreatePostCommentResponse savePostComment(
      PostComment postComment);


}
