package com.threadly.core.port.user.in.profile.query.dto;

import com.threadly.core.domain.follow.FollowStatusType;
import com.threadly.core.port.commons.dto.UserPreview;

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
