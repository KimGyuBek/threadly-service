package com.threadly.core.usecase.follow.query.dto;

import com.threadly.core.usecase.commons.dto.UserPreview;
import com.threadly.commons.response.CursorSupport;
import java.time.LocalDateTime;

public record FollowerResponse(
    UserPreview follower,
    LocalDateTime followedAt
) implements CursorSupport {


  @Override
  public LocalDateTime cursorTimeStamp() {
    return followedAt;
  }

  @Override
  public String cursorId() {
    return follower.userId();
  }
}
