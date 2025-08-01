package com.threadly.user.profile.register;


/**
 * UserProfileApiResponse Dto
 *
 * @param accessToken
 * @param refreshToken
 */
public record MyProfileRegisterApiResponse(
    String accessToken,
    String refreshToken
) {


}
