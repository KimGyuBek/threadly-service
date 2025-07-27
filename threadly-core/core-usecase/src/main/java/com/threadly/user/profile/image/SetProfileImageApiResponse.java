package com.threadly.user.profile.image;

/**
 * 사용자 프로필 설정 요청 API 응답 객체
 */
public record SetProfileImageApiResponse(
    String userProfileImageId,
    String imageUrl
) {

}
