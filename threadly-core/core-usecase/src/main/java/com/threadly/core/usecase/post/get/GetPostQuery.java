package com.threadly.core.usecase.post.get;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 게시글 상세 조회 요청을 위한 쿼리 객체
 */
@AllArgsConstructor
@Getter
public class GetPostQuery {
  private String postId;
  private String userId;

}
