package com.threadly.post.comment.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 게시물 댓글 생성 Command
 */
@Getter
@AllArgsConstructor
public class CreatePostCommentCommand {

  private String postId;
  private String userId;
  private String content;

}
