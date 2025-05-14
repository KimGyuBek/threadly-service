package com.threadly.posts;

import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

/**
 * 게시글 좋아요 도메인
 */
@Getter
@Builder
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
    return PostLike.builder()
        .postId(postId)
        .userId(userId)
        .build();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }

    if (object == null || getClass() != object.getClass()) {
      return false;
    }

    PostLike postLike = (PostLike) object;
    return Objects.equals(postId, postLike.postId)
        && Objects.equals(userId, postLike.userId);

  }

  @Override
  public int hashCode() {
    return Objects.hash(postId, userId);
  }
}
