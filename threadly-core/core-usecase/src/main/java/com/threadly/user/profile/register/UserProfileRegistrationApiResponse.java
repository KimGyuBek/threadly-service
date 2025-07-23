package com.threadly.user.profile.register;


/**
 * UserProfileApiResponse Dto
 *
 * @param accessToken
 * @param refreshToken
 */
public record UserProfileRegistrationApiResponse(
    String accessToken,
    String refreshToken
) {


}
