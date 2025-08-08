package com.threadly.post.comment.get;

import com.threadly.commons.dto.UserPreview;
import com.threadly.response.CursorSupport;
import java.time.LocalDateTime;

/**
 * 댓글 조회 API 응답 객체
 */
public record GetPostCommentApiResponse(
    String postId,
    String commentId,
    UserPreview commenter,
    LocalDateTime commentedAt,
    long likeCount,
    String content,
    boolean liked
) implements CursorSupport {

  @Override
  public LocalDateTime cursorTimeStamp() {
    return commentedAt;
  }

  @Override
  public String cursorId() {
    return commentId;
  }
}
