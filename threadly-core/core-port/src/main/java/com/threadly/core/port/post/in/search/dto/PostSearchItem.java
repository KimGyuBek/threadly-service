package com.threadly.core.port.post.in.search.dto;

import com.threadly.commons.response.CursorSupport;
import com.threadly.core.port.commons.dto.UserPreview;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시글 검색 결과  Dto
 */
public record PostSearchItem(
    String postId,
    UserPreview author,
    String content,
    List<PostImageItem> images,
    long likeCount,
    long commentCount,
    boolean liked,
    LocalDateTime postedAt
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
   * 게시글 검색 이미지 데이터
   */
  public record PostImageItem(
      String imageUrl,
      int imageOrder
  ) {


  }
}
