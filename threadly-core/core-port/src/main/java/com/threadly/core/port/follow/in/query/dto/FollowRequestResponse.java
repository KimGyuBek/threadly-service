package com.threadly.core.port.follow.in.query.dto;

import com.threadly.commons.response.CursorSupport;
import com.threadly.core.port.commons.dto.UserPreview;
import java.time.LocalDateTime;


/**
 * 팔로우 요청 목록 커서 기반 조회 API 응답 객체
 */
public record FollowRequestResponse(
    String followId,
    UserPreview requester,
    LocalDateTime followRequestedAt
) implements CursorSupport {

  @Override
  public LocalDateTime cursorTimeStamp() {
    return followRequestedAt;
  }

  @Override
  public String cursorId() {
    return followId;
  }
}
