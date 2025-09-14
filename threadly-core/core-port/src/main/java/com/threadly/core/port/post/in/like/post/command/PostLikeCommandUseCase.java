package com.threadly.core.port.post.in.like.post.command;

import com.threadly.core.port.post.in.like.post.command.dto.LikePostApiResponse;
import com.threadly.core.port.post.in.like.post.command.dto.LikePostCommand;

/**
 * 게시글 좋아요 command UseCase
 */
public interface PostLikeCommandUseCase {

  /**
   * 게시글 좋아요 취소
   * @param command
   * @return
   */
  LikePostApiResponse cancelLikePost(LikePostCommand command);

  /**
   * 게시글 좋아요
   * @param command
   * @return
   */
  LikePostApiResponse likePost(LikePostCommand command);

}
