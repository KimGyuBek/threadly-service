package com.threadly.user.profile.get;

/**
 * 사용자 프로필 조회 Api 응답 객체
 */
public record GetUserProfileApiResponse(
    String userId,
    String nickname,
    String statusMessage,
    String bio,
    String profileImageUrl
) {

}
