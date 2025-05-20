package com.threadly.post.comment.like;

import com.threadly.post.comment.like.response.CreatePostCommentLikeResponse;
import com.threadly.posts.comment.CommentLike;

/**
 * 게시글 댓글 좋아요 생성 관련 port
 */
public interface CreatePostCommentLikePort {

  void createPostCommentLike(CommentLike commentLike);
}
