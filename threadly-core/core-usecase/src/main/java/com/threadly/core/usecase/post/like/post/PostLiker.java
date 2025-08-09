package com.threadly.core.usecase.post.like.post;

import com.threadly.core.usecase.commons.dto.UserPreview;
import com.threadly.commons.response.CursorSupport;
import java.time.LocalDateTime;

/**
 * 게시글 좋아요 목록 조회 API 응답 DTO
 */
public record PostLiker(
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
