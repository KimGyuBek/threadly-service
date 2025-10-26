package com.threadly.core.port.auth.in.token.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * login token 응답 DTO
 * @param accessToken
 * @param refreshToken
 */
@Schema(description = "로그인 토큰 응답")
public record LoginTokenApiResponse(
    String accessToken,
    String refreshToken
) {


}
