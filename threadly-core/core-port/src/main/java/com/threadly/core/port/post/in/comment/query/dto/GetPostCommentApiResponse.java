package com.threadly.core.port.post.in.comment.query.dto;

import com.threadly.commons.response.CursorSupport;
import com.threadly.core.port.commons.dto.UserPreview;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * 댓글 조회 API 응답 객체
 */
@Schema(description = "댓글 조회 응답")
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
