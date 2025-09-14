package com.threadly.core.port.post.in.comment.command;

import com.threadly.core.port.post.in.comment.command.dto.CreatePostCommentApiResponse;
import com.threadly.core.port.post.in.comment.command.dto.CreatePostCommentCommand;
import com.threadly.core.port.post.in.comment.command.dto.DeletePostCommentCommand;

/**
 * 게시글 댓글 command 관련 usecase
 */
public interface PostCommentCommandUseCase {

  /**
   * 게시글 생성
   *
   * @return
   */
  CreatePostCommentApiResponse createPostComment(CreatePostCommentCommand command);

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
