package com.threadly.core.port.post.in.command.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 게시물 수정 Command
 */
@Getter
@AllArgsConstructor
public class UpdatePostCommand {

  private String postId;
  private String userId;
  private String content;

}
