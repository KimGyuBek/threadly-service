package com.threadly.post.create;

import java.time.LocalDateTime;

/**
 * 게시글 생성 API 응답 DTO
 * @param postId
 * @param userProfileImageUrl
 * @param userNickName
 * @param userId
 * @param content
 * @param likesCount
 * @param commentsCount
 * @param createdAt
 */
public record CreatePostApiResponse(
    String postId,
    String userProfileImageUrl,
    String userNickName,
    String userId,
    String content,
    long viewCount,
    int likesCount,
    int commentsCount,
    LocalDateTime createdAt

) {

}
