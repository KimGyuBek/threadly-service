package com.threadly.post.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 게시물 삭제 Command
 */
@Getter
@AllArgsConstructor
public class DeletePostCommand {

  private String postId;
  private String userId;

}
