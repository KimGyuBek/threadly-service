package com.threadly.post.comment.create;

import com.threadly.posts.Post;
import com.threadly.posts.comment.PostComment;
import com.threadly.user.User;

/**
 * 게시글 댓글 저장 관련 port
 */
public interface CreatePostCommentPort {

  /**
   * 게시글 댓글 저장
   * @param postComment
   * @return
   */
  CreatePostCommentResponse savePostComment(Post post,
      PostComment postComment, User user);


}
