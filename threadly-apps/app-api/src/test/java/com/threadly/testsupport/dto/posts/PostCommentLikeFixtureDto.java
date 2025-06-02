package com.threadly.testsupport.dto.posts;

import com.threadly.posts.PostCommentStatusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 게시글 댓글 좋아요 Fixture DTO 객체
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentLikeFixtureDto {

  private String commentId;
  private String userId;


}
