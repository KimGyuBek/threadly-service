package com.threadly.post.like.comment;

import com.threadly.commons.dto.UserPreview;
import com.threadly.response.CursorSupport;
import java.time.LocalDateTime;

/**
 * 게시글 댓글에 좋아요를 사용자 목록 API 응답 객체
 */
public record PostCommentLiker(
      UserPreview liker,
    LocalDateTime likedAt

) implements CursorSupport {

  @Override
  public LocalDateTime cursorTimeStamp() {
    return likedAt;
  }

  @Override
  public String cursorId() {
    return liker.userId();
  }
}
