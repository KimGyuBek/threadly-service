package com.threadly.core.port.follow.in.query.dto;

import com.threadly.commons.response.CursorSupport;
import com.threadly.core.port.commons.dto.UserPreview;
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
