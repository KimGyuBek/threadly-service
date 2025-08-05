package com.threadly.user.follow.query.dto;

import com.threadly.commons.dto.UserPreview;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 팔로워 목록 커서 기반 조회 API 응답 객체
 */
public record GetFollowersApiResponse(
    List<FollowerDetails> followers,
    NextCursor nextCursor
) {


  /**
   * 팔로워 목록 상세 정보
   */
  public record FollowerDetails(
      UserPreview follower,
      LocalDateTime followedAt
  ) {

  }

  /**
   * 다음 페이지 커서 기반 조회를 위한 Next Cursor 객체
   *
   * @param cursorFollowedAt
   * @param cursorFollowerId
   */
  public record NextCursor(LocalDateTime cursorFollowedAt, String cursorFollowerId) {

  }
}
