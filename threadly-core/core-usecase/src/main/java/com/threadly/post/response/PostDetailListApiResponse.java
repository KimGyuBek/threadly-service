package com.threadly.post.response;

import java.util.List;

/**
 * 게시글 리스트 조회 응답 API DTO
 */
public record PostDetailListApiResponse(
    List<PostDetailApiResponse> posts
) {

}
