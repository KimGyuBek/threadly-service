package com.threadly.post.status;

/**
 * 게시글 상호작용 관련 UseCase
 */
public interface GetPostStatusUseCase {

  /**
   * 게시글 좋아요/댓글 수 통계성 정보 조회
   * @param postId
   * @return
   */
  GetPostStatusApiResponse getStatus(String postId);

}
