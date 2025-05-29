package com.threadly.post.update;

import java.time.LocalDateTime;

/**
 * 게시글 수정 API 응답 DTO
 * @param postId
 * @param userProfileImageUrl
 * @param userNickName
 * @param userId
 * @param content
 * @param likesCount
 * @param commentsCount
 * @param createdAt
 */
public record UpdatePostApiResponse(
    String postId,
    String userProfileImageUrl,
    String userNickName,
    String userId,
    String content,
    int likesCount,
    int commentsCount,
    LocalDateTime createdAt

) {

}
