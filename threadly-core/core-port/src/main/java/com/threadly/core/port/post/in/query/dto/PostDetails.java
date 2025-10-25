package com.threadly.core.port.post.in.query.dto;

import com.threadly.commons.response.CursorSupport;
import com.threadly.core.port.commons.dto.UserPreview;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시글 조회 응답 APi DTO
 */
public record PostDetails(
    String postId,
    UserPreview author,
    String content,
    List<PostImage> images,
    long viewCount,
    LocalDateTime postedAt,
    long likeCount,
    long commentCount,
    boolean liked
) implements CursorSupport {

  @Override
  public LocalDateTime cursorTimeStamp() {
    return postedAt;
  }

  @Override
  public String cursorId() {
    return postId;
  }

  /**
   * 게시글 이미지 데이터
   *
   * @param imageUrl
   * @param imageOrder
   */
  public record PostImage(
      String imageId,
      String imageUrl,
      int imageOrder
  ) {


  }


}
