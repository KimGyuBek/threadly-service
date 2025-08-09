package com.threadly.follow.query.dto;

import com.threadly.commons.dto.UserPreview;
import com.threadly.response.CursorSupport;
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
