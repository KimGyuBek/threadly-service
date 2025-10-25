package com.threadly.core.port.user.in.search.dto;

import com.threadly.commons.response.CursorSupport;
import com.threadly.core.domain.follow.FollowStatus;
import com.threadly.core.port.commons.dto.UserPreview;
import java.time.LocalDateTime;

/**
 * 사용자 검색 결과 DTO
 */
public record UserSearchItem(
    UserPreview user,
    FollowStatus followStatus
) implements CursorSupport {

  @Override
  public LocalDateTime cursorTimeStamp() {
    return null;
  }

  @Override
  public String cursorId() {
    return user.nickname();
  }
}
