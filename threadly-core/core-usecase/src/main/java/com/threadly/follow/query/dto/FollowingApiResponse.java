package com.threadly.follow.query.dto;

import com.threadly.commons.dto.UserPreview;
import com.threadly.response.CursorSupport;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 팔로잉 목록 커서 기반 조회 API 응답 객체
 */
public record FollowingApiResponse(
    UserPreview following,
    LocalDateTime followingAt

) implements CursorSupport {

  @Override
  public LocalDateTime cursorTimeStamp() {
    return followingAt();
  }

  @Override
  public String cursorId() {
    return following.userId();
  }
}
