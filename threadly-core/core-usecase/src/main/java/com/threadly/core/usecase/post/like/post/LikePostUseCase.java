package com.threadly.core.usecase.post.like.post;

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
