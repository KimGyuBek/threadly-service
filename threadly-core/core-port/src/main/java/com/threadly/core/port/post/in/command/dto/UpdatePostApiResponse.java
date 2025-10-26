package com.threadly.core.port.post.in.command.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * 게시글 수정 API 응답 DTO
 */
@Schema(description = "게시글 수정 응답")
public record UpdatePostApiResponse(
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
