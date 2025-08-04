package com.threadly.post.get;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시글 리스트 조회 응답 API DTO
 * @param posts
 * @param nextCursor(null 이면 마지막 페이지)
 */
public record GetPostDetailsApiResponse(
    List<GetPostDetailApiResponse> posts,
    NextCursor nextCursor
) {

  /**
   * 다음 페이지 조회를 위한 커서 정보 객체
   * @param postedAt
   * @param postId
   */
  public record NextCursor(LocalDateTime postedAt, String postId) {

  }
}
