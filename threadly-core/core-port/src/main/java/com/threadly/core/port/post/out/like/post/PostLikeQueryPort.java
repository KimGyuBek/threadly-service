package com.threadly.core.port.post.out.like.post;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시글 좋아요 keyword 관련  Port
 */
public interface PostLikeQueryPort {

  /**
   * postId, userId에 행당하는 게시글 좋아요가 있는지 검증
   *
   * @param postId
   * @param userId
   * @return
   */
  boolean existsByPostIdAndUserId(String postId, String userId);

  /**
   * postId에 해당하는 좋아요 수 조회
   *
   * @param postId
   * @return
   */
  long fetchLikeCountByPostId(String postId);

  /**
   * 특정 게시글에 좋아요를 누른 사람 목록을 커서 기반으로 조회
   *
   * @param postId
   * @param cursorLikedAt
   * @param likerId
   * @param limit
   * @return
   */
  List<PostLikerProjection> fetchPostLikersBeforeCreatedAt(String postId,
      LocalDateTime cursorLikedAt, String likerId, int limit);

}
