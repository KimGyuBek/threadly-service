package com.threadly.core.port.post.in.create;

import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 게시물 생성 Command
 */
@Getter
public class CreatePostCommand {

  private String userId;
  private String content;
  private List<ImageCommand> images;

  public CreatePostCommand(String userId, String content, List<ImageCommand> images) {
    this.userId = userId;
    this.content = content;
    this.images = images != null ? images : Collections.emptyList();
  }

  /**
   * 게시글 이미지 Command
   */
  @Getter
  @AllArgsConstructor
  public static class ImageCommand {
    private String imageId;
    private int imageOrder;
  }
}
