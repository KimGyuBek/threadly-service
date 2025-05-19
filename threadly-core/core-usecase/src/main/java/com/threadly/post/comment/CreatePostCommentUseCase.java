package com.threadly.post.comment;

import com.threadly.post.comment.command.CreatePostCommentCommand;
import com.threadly.post.comment.response.CreatePostCommentApiResponse;

/**
 * 댓글 생성 관련 UseCase
 */
public interface CreatePostCommentUseCase {

  /**
   * 게시글 생성
   *
   * @return
   */
  CreatePostCommentApiResponse createPostComment(CreatePostCommentCommand command);
}
