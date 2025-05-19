package com.threadly.post.comment;

import com.threadly.post.comment.response.CreatePostCommentResponse;
import com.threadly.posts.Post;
import com.threadly.posts.comment.PostComment;
import com.threadly.user.User;

/**
 * 게시글 댓글 저장 관련 port
 */
public interface SavePostCommentPort {

  /**
   * 게시글 댓글 저장
   * @param postComment
   * @return
   */
  CreatePostCommentResponse savePostComment(Post post,
      PostComment postComment, User user);


}
