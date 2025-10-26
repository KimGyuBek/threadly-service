package com.threadly.core.port.user.in.profile.query.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 내 프로필 수정용 Api 응답 객체
 */
@Schema(description = "내 프로필 상세 조회 응답")
public record GetMyProfileDetailsApiResponse(
    String userId,
    String nickname,
    String statusMessage,
    String bio,
    String phone,
    String profileImageId,
    String profileImageUrl
) {

}
