package com.threadly.post.like.comment;

import com.threadly.commons.dto.UserPreview;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시글 댓글에 좋아요를 사용자 목록 API 응답 객체
 */
public record GetPostCommentLikersApiResponse(
    List<PostCommentLiker> likers,
    NextCursor nextCursor

) {

  /**
   * 게시글 댓글에 좋아요를 누른 사용자 정보 응답 객체
   */
  public record PostCommentLiker(
      UserPreview liker,
      String likerBio,
      LocalDateTime likedAt
  ) {


  }

  /**
   * 커서 객체
   * @param cursorLikedAt
   * @param cursorLikerId
   */
  public record NextCursor(
      LocalDateTime cursorLikedAt,
      String cursorLikerId
  ) {


  }
}
