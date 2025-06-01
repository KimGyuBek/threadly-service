package com.threadly.testsupport.dto.posts;

import com.threadly.posts.PostCommentStatusType;
import com.threadly.posts.PostStatusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 게시글 댓글 Fixture DTO 객체
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentFixtureDto {

  private String commentId;
  private String postId;
  private String userId;
  private String content;
  private PostCommentStatusType status;


}
