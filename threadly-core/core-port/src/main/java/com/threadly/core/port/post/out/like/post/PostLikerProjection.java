package com.threadly.core.port.post.out.like.post;

import java.time.LocalDateTime;

/**
 * 게시글 좋아요 사용자 조회 응답 객체
 */
public interface PostLikerProjection {

  String getLikerId();

  String getLikerNickname();

  String getLikerProfileImageUrl();

  String getLikerBio();

  LocalDateTime getLikedAt();

}
