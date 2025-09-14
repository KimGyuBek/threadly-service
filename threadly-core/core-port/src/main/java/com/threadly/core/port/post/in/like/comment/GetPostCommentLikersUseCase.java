package com.threadly.core.port.post.in.like.comment;

import com.threadly.commons.response.CursorPageApiResponse;

/**
 * 댓글 좋아요 목롲 조회 관련 UseCase
 */
public interface GetPostCommentLikersUseCase {

  /**
   * 게시글 댓글 좋아요 목록 커서 기반 조회
   *
   * @param query
   * @return
   */
  CursorPageApiResponse<PostCommentLiker> getPostCommentLikers(
      GetPostCommentLikersQuery query);

}
