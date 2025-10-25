package com.threadly.core.port.post.in.query.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 게시글 좋아요 활동 조회를 위한 쿼리 객체
 * <p>
 * 특정 게시글의 좋아요 수, 좋아요 목록 등 게시글의 좋아요 관련 정보를 조회할때 사용됨
 */
@AllArgsConstructor
@Getter
public class GetPostEngagementQuery {

  private String postId;
  private String userId;

}
