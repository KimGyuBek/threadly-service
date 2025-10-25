package com.threadly.core.port.user.in.profile.query.dto;

/**
 * 내 프로필 수정용 Api 응답 객체
 */
public record GetMyProfileDetailsApiResponse(
    String userId,
    String nickname,
    String statusMessage,
    String bio,
    String phone,
    String profileImageId,
    String profileImageUrl
) {

}
