package com.threadly.post.comment.get;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시글 댓글 목록 조회 API 응답 객체
 */
public record GetPostCommentsApiResponse(
    List<GetPostCommentApiResponse> comments,
    NextCursor nextCursor
) {

  /**
   * 다음 페이지 조회를 위한 커서 정보 객체
   * @param cursorCommentedAt
   * @param cursorCommentId
   */
  public record NextCursor(
      LocalDateTime cursorCommentedAt,
      String cursorCommentId
  ) {

  }
}
