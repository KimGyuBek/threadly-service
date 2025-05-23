package com.threadly.post.query;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 게시글 상세 조회 요청 용 DTO
 */
@AllArgsConstructor
@Getter
public class GetPostQuery {
  private String postId;
  private String userId;

}
