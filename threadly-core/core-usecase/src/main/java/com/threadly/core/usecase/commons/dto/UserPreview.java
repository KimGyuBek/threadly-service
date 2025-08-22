package com.threadly.core.usecase.commons.dto;

/**
 * 사용자 정보 프리뷰 dto
 */
public record UserPreview(
    String userId,
    String nickname,
    String profileImageUrl
) {

}
