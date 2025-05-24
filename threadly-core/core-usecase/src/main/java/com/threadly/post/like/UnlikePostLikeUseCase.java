package com.threadly.post.like;

import com.threadly.post.like.command.LikePostCommand;
import com.threadly.post.like.response.LikePostApiResponse;

/**
 * 게시글 좋아요 취소 UseCase
 */
public interface UnlikePostLikeUseCase {

  /**
   * 게시글 좋아요 취소
   * @param command
   * @return
   */
  LikePostApiResponse cancelLikePost(LikePostCommand command);


}
