package com.threadly.post;

import com.threadly.post.response.PostStatusApiResponse;

/**
 * 게시글 상호작용 관련 UseCase
 */
public interface PostStatusUseCase {

  /**
   * 게시글 좋아요/댓글 수 통계성 정보 조회
   * @param postId
   * @return
   */
  PostStatusApiResponse getStatus(String postId);

}
