package com.threadly.post.get;

import com.threadly.commons.dto.UserPreview;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시글 조회 응답 APi DTO
 */
public record GetPostDetailApiResponse(
    String postId,
    UserPreview author,
    String content,
    List<PostImage> images,
    long viewCount,
    LocalDateTime postedAt,
    long likeCount,
    long commentCount,
    boolean liked
) {


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
