package com.threadly.core.port.user.in.profile.command.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 내 프로필 등록 API 응답 객체
 *
 * @param accessToken
 * @param refreshToken
 */
@Schema(description = "프로필 등록 응답")
public record RegisterMyProfileApiResponse(
    String accessToken,
    String refreshToken
) {


}
