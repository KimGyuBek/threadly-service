package com.threadly.core.usecase.post.comment.delete;

/**
 * 게시글 댓글 변경 관련 UseCase
 */
public interface DeletePostCommentUseCase {

  /**
   * DELETED 상태로 변경
   *
   * @param command
   */
  void softDeletePostComment(DeletePostCommentCommand command);

  /**
   * postId에 해당하는 댓글 및 좋아요 목록 삭제
   *
   * @param postId
   */
  void deleteAllCommentsAndLikesByPostId(String postId);

}
