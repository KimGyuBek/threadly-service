package com.threadly.auth.token.response;


/**
 * login token 응답 DTO
 * @param accessToken
 * @param refreshToken
 */
public record LoginTokenResponse(
    String accessToken,
    String refreshToken
) {


}
