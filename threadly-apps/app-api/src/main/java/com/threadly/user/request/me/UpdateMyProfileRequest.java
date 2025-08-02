package com.threadly.user.request.me;

import com.threadly.user.profile.update.UpdateMyProfileCommand;

/**
 * 사용자 프로필 업데이트 요청 객체
 */
public record UpdateMyProfileRequest(
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
    public UpdateMyProfileCommand toCommand(String userId) {
        return new UpdateMyProfileCommand(
            userId,
            nickname,
            statusMessage,
            bio,
            phone,
            profileImageId
        );
    }

}
