package com.threadly.core.port.post.in.comment.query;


import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.port.post.in.comment.query.dto.GetPostCommentApiResponse;
import com.threadly.core.port.post.in.comment.query.dto.GetPostCommentDetailQuery;
import com.threadly.core.port.post.in.comment.query.dto.GetPostCommentListQuery;

/**
 * 게시글 댓글 keyword 관련 UseCase
 */
public interface PostCommendQueryUseCase {

  /**
   * 게시글의 댓글 상세 정보 리스트를 커서 기반으로 조회한다.
   * <p>
   * 좋아요 여부, 작성자 정보 등 사용자 기준 정보가 포함된다.
   *
   * @param query
   * @return
   */
  CursorPageApiResponse<GetPostCommentApiResponse> getPostCommentDetailListForUser(
      GetPostCommentListQuery query);


  /**
   * 게시글 댓글 상세 정보를 사용자 기준으로 조회한다.
   * <p>
   * 댓글 작성자 정보, 작성 시간, 좋아요 여부 등의 정보를 포함한다.
   *
   * @param query
   * @return 댓글 상세 정보
   */
  GetPostCommentApiResponse getPostCommentDetailForUser(GetPostCommentDetailQuery query);

}
