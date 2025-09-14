package com.threadly.core.port.user.in.profile.image.dto;

/**
 * 사용자 프로필 설정 요청 API 응답 객체
 */
public record UploadMyProfileImageApiResponse(
    String userProfileImageId,
    String imageUrl
) {

}
