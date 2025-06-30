package com.threadly.post.comment.create;

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
