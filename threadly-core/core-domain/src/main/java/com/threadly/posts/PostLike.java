package com.threadly.posts;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 게시글 좋아요 도메인
 */
@Getter
@AllArgsConstructor
public class PostLike {

  private String postId;
  private String userId;

  /**
   * 새로운 좋아요 생성
   *
   * @param postId
   * @param userId
   * @return
   */
  public static PostLike newLike(String postId, String userId) {
    return new PostLike(postId, userId);
  }

}
