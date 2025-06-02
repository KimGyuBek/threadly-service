package com.threadly.testsupport.dto.posts;

import com.threadly.posts.PostStatusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 게시글 Fixture DTO 객체
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostFixtureDto {

  private String postId;
  private String userId;
  private String content;
  private int viewCount;
  private PostStatusType status;



}
