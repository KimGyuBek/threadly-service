package com.threadly.core.port.user.in.profile.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 사용자 프로필 설정 요청 API 응답 객체
 */
@Schema(description = "프로필 이미지 업로드 응답")
public record UploadMyProfileImageApiResponse(
    String userProfileImageId,
    String imageUrl
) {

}
