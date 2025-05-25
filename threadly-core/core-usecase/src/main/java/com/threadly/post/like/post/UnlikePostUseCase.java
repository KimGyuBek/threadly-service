package com.threadly.post.like.post;

/**
 * 게시글 좋아요 취소 UseCase
 */
public interface UnlikePostUseCase {

  /**
   * 게시글 좋아요 취소
   * @param command
   * @return
   */
  LikePostApiResponse cancelLikePost(LikePostCommand command);


}
