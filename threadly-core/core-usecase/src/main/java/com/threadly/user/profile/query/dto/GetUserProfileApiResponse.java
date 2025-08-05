package com.threadly.user.profile.query.dto;

import com.threadly.commons.dto.UserPreview;
import com.threadly.user.FollowStatusType;

/**
 * 사용자 프로필 조회 Api 응답 객체
 */
public record GetUserProfileApiResponse(
    UserPreview user,
    String statusMessage,
    String bio,
    FollowStatusType followStatusType
) {

}
