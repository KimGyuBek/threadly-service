package com.threadly.post;

import com.threadly.post.command.CreatePostCommand;
import com.threadly.post.command.DeletePostCommand;
import com.threadly.post.command.UpdatePostCommand;
import com.threadly.post.response.CreatePostApiResponse;
import com.threadly.post.response.UpdatePostApiResponse;

/**
 * 게시글 수정 관련 UseCase
 */
public interface UpdatePostUseCase {

  /**
   * 게시글 수정
   * @param command
   * @return
   */
  UpdatePostApiResponse updatePost(UpdatePostCommand command);

  /**
   * 게시글 삭제 상태로 변경
   * @param command
   */
  void deletePost(DeletePostCommand command);

}
