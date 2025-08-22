package com.threadly.core.usecase.follow.command.dto;

import com.threadly.core.domain.follow.FollowStatusType;

/**
 * 샤용자 팔로우 요청 API 응답 객체
 */
public record FollowUserApiResponse(
    FollowStatusType followStatusType
) {

}
