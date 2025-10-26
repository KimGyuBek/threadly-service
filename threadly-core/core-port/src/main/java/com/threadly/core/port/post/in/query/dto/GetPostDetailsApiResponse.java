package com.threadly.core.port.post.in.query.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시글 리스트 조회 응답 API DTO
 * @param posts
 * @param nextCursor(null 이면 마지막 페이지)
 */
@Schema(description = "게시글 목록 조회 응답")
public record GetPostDetailsApiResponse(
    List<PostDetails> posts,
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
