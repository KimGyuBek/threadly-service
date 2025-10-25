package com.threadly.core.port.post.in.like.post.command.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 게시글 좋아요 요청 Command
 */
@Getter
@AllArgsConstructor
public class LikePostCommand {

  private String postId;
  private String userId;

}
