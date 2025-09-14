package com.threadly.core.port.post.out.like.comment;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시글 댓글 좋아요 조회 관련 port
 */
public interface FetchPostCommentLikePort {

  /**
   * 사용자가 좋아요를 눌렀는지 조회
   *
   * @param commentId
   * @param userId
   * @return
   */
  boolean existsByCommentIdAndUserId(String commentId, String userId);

  /**
   * commentId에 해당하는 좋아요 수 조회
   *
   * @param commentId
   * @return
   */
  long fetchLikeCountByCommentId(String commentId);

  /**
    지정된 commentId에 대한 특정 댓글 좋아요 목록을 커서 기반으로 조회한다.
   * <p>
   * 댓글은 likedAt likerId 기준으로 내림차순 정렬되며,커서 기준 이전 댓글들만 조회된다.
   * <p>
   * 커서가 null인 경우 가장 최신 댓글부터 조회하며, 최대 {@code limit}개 까지 조회된댜.
   *
   * @param commentId
   * @param cursorLikedAt
   * @param likerId
   * @return
   */
  List<PostCommentLikerProjection> fetchCommentLikerListByCommentIdWithCursor(String commentId,
       LocalDateTime cursorLikedAt, String likerId, int limit);


}
