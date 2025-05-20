package com.threadly.posts.comment;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 게시글 댓글 좋아요 도메인
 */
@Getter
@Builder
@AllArgsConstructor
public class CommentLike {

  private String commentId;
  private String userId;

  /**
   * 댓글 좋아요 생성
   *
   * @param commentId
   * @param userId
   * @return
   */
  public static CommentLike newLike(String commentId, String userId) {
    return CommentLike.builder()
        .commentId(commentId)
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

    CommentLike commentLike = (CommentLike) object;
    return Objects.equals(commentId, commentLike.commentId)
        && Objects.equals(userId, commentLike.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(commentId, userId);
  }
}
