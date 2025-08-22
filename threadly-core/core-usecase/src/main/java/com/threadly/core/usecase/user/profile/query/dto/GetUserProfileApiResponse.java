package com.threadly.core.usecase.user.profile.query.dto;

import com.threadly.core.usecase.commons.dto.UserPreview;
import com.threadly.core.domain.follow.FollowStatusType;

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
