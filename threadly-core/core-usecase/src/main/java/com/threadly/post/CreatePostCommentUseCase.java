package com.threadly.post;

import com.threadly.post.command.CreatePostCommentCommand;
import com.threadly.post.response.CreatePostCommentApiResponse;

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
