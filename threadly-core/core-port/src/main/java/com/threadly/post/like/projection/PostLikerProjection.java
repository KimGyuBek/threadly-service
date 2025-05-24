package com.threadly.post.like.projection;

import java.time.LocalDateTime;

/**
 * 게시글 좋아요 목록 조회 응답 객체
 */
public interface PostLikerProjection {

  String getLikerId();

  String getLikerNickname();

  String getLikerProfileImageUrl();

  String getLikerBio();

  LocalDateTime getLikedAt();

}
