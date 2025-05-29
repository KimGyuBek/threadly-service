package com.threadly.post.create;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 게시물 생성 Command
 */
@Getter
@AllArgsConstructor
public class CreatePostCommand {

  private String userId;
  private String content;

}
