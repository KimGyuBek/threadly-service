package com.threadly.post.like.comment;

import java.time.LocalDateTime;

/**
 * 게시글 댓글 좋아요 사용자 조회 응답 객체
 */
public interface PostCommentLikerProjection {
  String getLikerId();

  String getLikerNickname();

  String getLikerProfileImageUrl();

  String getLikerBio();

  LocalDateTime getLikedAt();

}
