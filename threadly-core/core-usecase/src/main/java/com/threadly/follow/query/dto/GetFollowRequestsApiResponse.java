package com.threadly.follow.query.dto;

import com.threadly.commons.dto.UserPreview;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 팔로우 요청 목록 커서 기반 조회 API 응답 객체
 */
public record GetFollowRequestsApiResponse(
    List<FollowRequestDetails> followRequests,
    NextCursor nextCursor
) {


  /**
   * 팔로우 요청 상세 정보
   */
  public record FollowRequestDetails(
      String followId,
      UserPreview requester,
      LocalDateTime followRequestedAt
  ) {

  }

  /**
   * 다음 페이지 커서 기반 조회를 위한 Next Cursor 객체
   *
   * @param cursorFollowRequestedAt
   * @param cursorFollowId
   */
  public record NextCursor(LocalDateTime cursorFollowRequestedAt, String cursorFollowId) {

  }
}
