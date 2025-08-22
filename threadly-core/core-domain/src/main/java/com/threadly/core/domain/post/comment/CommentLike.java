package com.threadly.core.domain.post.comment;

import com.google.common.annotations.VisibleForTesting;
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

  /*Test*/

  /**
   * 테스트용
   * @param commentId
   * @param userId
   * @return
   */
  @VisibleForTesting
  public static CommentLike newTestCommentLike(String commentId, String userId) {
    return new CommentLike(
        commentId, userId
    );
  }

}
