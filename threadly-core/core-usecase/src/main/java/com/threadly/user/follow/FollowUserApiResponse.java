package com.threadly.user.follow;

import com.threadly.user.FollowStatusType;

/**
 * 샤용자 팔로우 요청 API 응답 객체
 */
public record FollowUserApiResponse(
    FollowStatusType followStatusType
) {

}
