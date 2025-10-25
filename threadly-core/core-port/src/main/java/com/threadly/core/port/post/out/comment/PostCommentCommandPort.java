package com.threadly.core.port.post.out.comment;

import com.threadly.core.domain.post.PostCommentStatus;
import com.threadly.core.domain.post.comment.PostComment;

/**
 * 게시글 댓글 command 관련 Port
 */
public interface PostCommentCommandPort {

  /**
   * 게시글 댓글 저장
   *
   * @param postComment
   * @return
   */
  void savePostComment(PostComment postComment);

  /**
   * 게시글 댓글 상태 변경
   *
   * @param commentId
   * @param status
   */
  void updatePostCommentStatus(String commentId, PostCommentStatus status);

  /**
   * postId에 해당하는 댓글들 상태 변경
   *
   * @param postId
   * @param status
   */
  void updateAllCommentStatusByPostId(String postId, PostCommentStatus status);

}
