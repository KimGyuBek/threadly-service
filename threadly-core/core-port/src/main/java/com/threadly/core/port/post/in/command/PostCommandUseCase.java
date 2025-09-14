package com.threadly.core.port.post.in.command;

import com.threadly.core.port.post.in.command.dto.CreatePostApiResponse;
import com.threadly.core.port.post.in.command.dto.CreatePostCommand;
import com.threadly.core.port.post.in.command.dto.DeletePostCommand;
import com.threadly.core.port.post.in.command.dto.UpdatePostApiResponse;
import com.threadly.core.port.post.in.command.dto.UpdatePostCommand;

/**
 * 게시글 command 관련 usecase
 */
public interface PostCommandUseCase {

  /**
   * 게시글 생성
   *
   * @param command
   * @return
   */
  CreatePostApiResponse createPost(CreatePostCommand command);

  /**
   * 게시글 삭제 상태로 변경
   *
   * @param command
   */
  void softDeletePost(DeletePostCommand command);

  /**
   * 게시글 수정
   *
   * @param command
   * @return
   */
  UpdatePostApiResponse updatePost(UpdatePostCommand command);
}
