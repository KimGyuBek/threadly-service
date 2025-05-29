package com.threadly.post.like.post;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 게시글을 좋아요 누른 사용자 조회 쿼리 객체
 */
@Getter
@AllArgsConstructor
public class GetPostLikersQuery {
  private String postId;
  private LocalDateTime cursorLikedAt;
  private String cursorLikerId;
  private int limit;


}
