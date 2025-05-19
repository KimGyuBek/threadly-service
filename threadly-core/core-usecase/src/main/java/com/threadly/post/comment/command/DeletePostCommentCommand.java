package com.threadly.post.comment.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 게시글 댓글 삭제 Command
 */
@Getter
@AllArgsConstructor
public class DeletePostCommentCommand {

  private String userId;
  private String postId;
  private String commentId;

}
