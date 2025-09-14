package com.threadly.core.port.post.in.query.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 게시글 목록 조회  DTO
 */
@Getter
@AllArgsConstructor
public class GetPostListQuery {
  private String userId;
  private LocalDateTime cursorPostedAt;
  private String cursorPostId;
  private int limit;
}
