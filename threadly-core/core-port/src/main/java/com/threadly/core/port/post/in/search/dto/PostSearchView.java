package com.threadly.core.port.post.in.search.dto;

import com.threadly.commons.response.CursorSupport;
import com.threadly.core.port.commons.dto.UserPreview;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시글 검색 결과 view Dto
 */
public record PostSearchView(
    String postId,
    UserPreview author,
    String content,
    List<PostImagePreview> images,
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
  public record PostImagePreview(
      String imageUrl,
      int imageOrder
  ) {


  }
}
