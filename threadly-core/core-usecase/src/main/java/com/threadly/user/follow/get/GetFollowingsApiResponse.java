package com.threadly.user.follow.get;

import com.threadly.commons.dto.UserPreview;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 팔로잉 목록 커서 기반 조회 API 응답 객체
 */
public record GetFollowingsApiResponse(
    List<FollowingDetails> followings,
    NextCursor nextCursor
) {


  /**
   * 팔로잉 목록 상세 정보
   */
  public record FollowingDetails(
      UserPreview following,
      LocalDateTime followedAt
  ) {

  }

  /**
   * 다음 페이지 커서 기반 조회를 위한 Next Cursor 객체
   *
   * @param cursorFollowedAt
   * @param cursorFollowingId
   */
  public record NextCursor(LocalDateTime cursorFollowedAt, String cursorFollowingId) {

  }
}
