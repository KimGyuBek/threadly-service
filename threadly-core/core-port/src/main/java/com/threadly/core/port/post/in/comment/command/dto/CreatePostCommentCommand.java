package com.threadly.core.port.post.in.comment.command.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 게시물 댓글 생성 Command
 */
@Getter
@AllArgsConstructor
public class CreatePostCommentCommand {

  private String postId;
  private String commenterId;
  private String content;

}
