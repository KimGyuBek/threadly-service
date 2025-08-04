package com.threadly.post.like.post;

import com.threadly.commons.dto.UserPreview;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시글 좋아요 목록 조회 API 응답 DTO
 */
public record GetPostLikersApiResponse(
    List<PostLiker> postLikers,
    LocalDateTime cursorLikedAt,
    String cursorLikerId

) {

  /**
   * 게시글에 좋아요를 누른 사용자 API 응답 DTO
   */
  public record PostLiker(
      UserPreview liker,
      String likerBio,
      LocalDateTime likedAt
  ) {


  }


}
