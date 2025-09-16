package com.threadly.core.port.post.out.like.post;

import com.threadly.core.domain.post.PostLike;

/**
 * 게시글 좋아요 command 관련 Port
 */
public interface PostLikeCommandPort {

  /**
   * 게시글 좋아요 저장
   * @param postLike
   */
  void createPostLike(PostLike postLike);

  /**
   * posId와 userId에 해당하는 좋아요 삭제
   * @param postId
   * @param userId
   * @return
   */
  int deleteByPostIdAndUserId(String postId, String userId);

  /**
   * 특정 게시글의 좋아요 전체 삭제
   * @param postId
   */
  void deleteAllByPostId(String postId);

}
