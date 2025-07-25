package com.threadly.user.profile.get;

/**
 * 내 프로필 수정용 Api 응답 객체
 */
public record GetMyProfileEditApiResponse(
    String userId,
    String nickname,
    String statusMessage,
    String bio,
    String phone,
    String profileImageUrl
) {

}
