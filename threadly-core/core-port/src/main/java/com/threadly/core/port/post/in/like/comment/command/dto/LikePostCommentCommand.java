package com.threadly.core.port.post.in.like.comment.command.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 게시글 댓글 좋아요 Command
 */
@Getter
@AllArgsConstructor
public class LikePostCommentCommand {

  private String commentId;
  private String userId;

}
