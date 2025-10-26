package com.threadly.core.port.user.in.profile.query.dto;

import com.threadly.core.domain.follow.FollowStatus;
import com.threadly.core.port.commons.dto.UserPreview;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 사용자 프로필 조회 Api 응답 객체
 */
@Schema(description = "사용자 프로필 조회 응답")
public record GetUserProfileApiResponse(
    UserPreview user,
    String statusMessage,
    String bio,
    FollowStatus followStatus
) {

}
