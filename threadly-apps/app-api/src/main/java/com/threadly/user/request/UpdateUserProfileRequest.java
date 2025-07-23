package com.threadly.user.request;

/**
 * 사용자 프로필 업데이트 요청 객체
 */
public record UpdateUserProfileRequest(
    String nickname,
    String statusMessage,
    String bio,
    String phone
    ) {

}
