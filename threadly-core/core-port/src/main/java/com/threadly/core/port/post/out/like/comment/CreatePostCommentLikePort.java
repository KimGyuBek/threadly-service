package com.threadly.core.port.post.out.like.comment;

import com.threadly.core.domain.post.comment.CommentLike;

/**
 * 게시글 댓글 좋아요 생성 관련 port
 */
public interface CreatePostCommentLikePort {

  void createPostCommentLike(CommentLike commentLike);
}
