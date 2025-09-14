package com.threadly.core.port.post.in.create;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시글 생성 API 응답 DTO
 * @param postId
 * @param userProfileImageUrl
 * @param userNickName
 * @param userId
 * @param content
 * @param postedAt
 */
public record CreatePostApiResponse(
    String postId,
    String userProfileImageUrl,
    String userNickName,
    String userId,
    String content,
    List<PostImageApiResponse> images,
    LocalDateTime postedAt

) {

  /**
   * 게시글 이미지 정보 API 응답
   */
  public record PostImageApiResponse(
      String imageId,
      String imageUrl,
      int imageOrder
  ) {


  }

}
