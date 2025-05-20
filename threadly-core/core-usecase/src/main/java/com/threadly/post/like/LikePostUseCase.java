package com.threadly.post.like;

import com.threadly.post.like.command.LikePostCommand;
import com.threadly.post.like.response.LikePostApiResponse;

/**
 * 게시글 좋아요 관련 UseCase
 */
public interface LikePostUseCase {

  /**
   * 게시글 좋아요
   * @param command
   * @return
   */
  LikePostApiResponse likePost(LikePostCommand command);



}
