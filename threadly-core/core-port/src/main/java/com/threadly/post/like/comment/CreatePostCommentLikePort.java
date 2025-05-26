package com.threadly.post.like.comment;

import com.threadly.posts.comment.CommentLike;

/**
 * 게시글 댓글 좋아요 생성 관련 port
 */
public interface CreatePostCommentLikePort {

  void createPostCommentLike(CommentLike commentLike);
}
