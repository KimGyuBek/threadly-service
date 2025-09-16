package com.threadly.core.port.post.out.like.comment;

import com.threadly.core.domain.post.comment.CommentLike;

/**
 * 게시글 댓글 좋아요 command 관련 port
 */
public interface PostCommentLikerCommandPort {

  /**
   * 댓글 좋아요 삭제
   *
   * @param commentId
   * @param userId
   */
  void deletePostCommentLike(String commentId, String userId);


  /**
   * postId에 해당하는 댓글들의 좋아요 목록 전체 삭제
   *
   * @param postId
   */
  void deleteAllByPostId(String postId);

  /**
   * 새로운 게시글 댓글 좋아요 저장
   *
   * @param commentLike
   */
  void createPostCommentLike(CommentLike commentLike);


}
