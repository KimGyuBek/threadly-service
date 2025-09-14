package com.threadly.core.port.post.in.like.post;

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
