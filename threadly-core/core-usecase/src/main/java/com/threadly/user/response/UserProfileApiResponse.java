package com.threadly.user.response;


/**
 * UserProfileApiResponse Dto
 * @param userName
 */
public record UserProfileApiResponse(
     String userName,
     String nickname,
     String statusMessage,
     String bio,
     String gender,
     String profileImageUrl
) {


}
