package com.threadly.post.get;

import java.time.LocalDateTime;

/**
 * 게시글 조회 응답 APi DTO
 */
public record GetPostDetailApiResponse(
    String postId,
    String userId,
    String userProfileImageUrl,
    String userNickname,
    String content,
    long viewCount,
    LocalDateTime postedAt,
    long likeCount,
    long commentCount,
    boolean liked
) {


}
