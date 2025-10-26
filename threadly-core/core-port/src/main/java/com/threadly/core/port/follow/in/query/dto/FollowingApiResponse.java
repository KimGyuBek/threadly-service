package com.threadly.core.port.follow.in.query.dto;

import com.threadly.commons.response.CursorSupport;
import com.threadly.core.port.commons.dto.UserPreview;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * 팔로잉 목록 커서 기반 조회 API 응답 객체
 */
@Schema(description = "팔로잉 목록 조회 응답")
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
