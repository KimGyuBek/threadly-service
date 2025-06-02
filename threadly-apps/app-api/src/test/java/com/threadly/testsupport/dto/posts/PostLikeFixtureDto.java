package com.threadly.testsupport.dto.posts;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 게시글 좋아요 Fixture DTO 객체
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeFixtureDto {

  private String postId;
  private String userId;


}
