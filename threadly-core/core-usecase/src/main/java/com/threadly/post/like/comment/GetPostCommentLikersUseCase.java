package com.threadly.post.like.comment;

/**
 * 댓글 좋아요 목롲 조회 관련 UseCase
 */
public interface GetPostCommentLikersUseCase {

  /**
   * 게시글 댓글 좋아요 목록 커서 기반 조회
   * @param query
   * @return
   */
  GetPostCommentLikersApiResponse getPostCommentLikers(GetPostCommentLikersQuery query);

}
