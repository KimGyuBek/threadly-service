package com.threadly.post.comment;

import com.threadly.posts.comment.PostComment;
import java.util.Optional;

/**
 * 게시글 댓글 조회 관련 Port
 */
public interface FetchPostCommentPort {

  /**
   * commentid로 게시글 댓글 조회
   * @param postCommentId
   * @return
   */
  Optional<PostComment> findById(String commentId);


}
