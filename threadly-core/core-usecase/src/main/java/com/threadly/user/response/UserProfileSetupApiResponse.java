package com.threadly.user.response;


/**
 * UserProfileApiResponse Dto
 *
 * @param accessToken
 * @param refreshToken
 */
public record UserProfileSetupApiResponse(
    String accessToken,
    String refreshToken
) {


}
