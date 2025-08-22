package com.threadly.core.usecase.user.profile.command.dto;


/**
 * 내 프로필 등록 API 응답 객체
 *
 * @param accessToken
 * @param refreshToken
 */
public record RegisterMyProfileApiResponse(
    String accessToken,
    String refreshToken
) {


}
