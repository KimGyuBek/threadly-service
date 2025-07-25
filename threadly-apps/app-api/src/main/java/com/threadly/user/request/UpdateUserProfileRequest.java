package com.threadly.user.request;

import com.threadly.user.profile.update.UpdateUserProfileCommand;

/**
 * 사용자 프로필 업데이트 요청 객체
 */
public record UpdateUserProfileRequest(
    String nickname,
    String statusMessage,
    String bio,
    String phone,
    String profileImageId
    ) {

    /**
     * request -> command
     * @param userId
     * @return
     */
    public  UpdateUserProfileCommand toCommand(String userId) {
        return new UpdateUserProfileCommand(
            userId,
            nickname,
            statusMessage,
            bio,
            phone,
            profileImageId
        );
    }

}
