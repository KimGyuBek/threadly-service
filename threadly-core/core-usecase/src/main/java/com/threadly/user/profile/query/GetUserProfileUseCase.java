package com.threadly.user.profile.query;

import com.threadly.user.profile.query.dto.GetMyProfileDetailsApiResponse;
import com.threadly.user.profile.query.dto.GetUserProfileApiResponse;

public interface GetUserProfileUseCase {

  /**
   * userId에 해당하는 profile 존재 여부 조회
   * @param userId
   * @return
   */
  boolean existsUserProfile(String userId);

  /**
   * nickname 중복 검증
   * @param nickname
   */
  void validateNicknameUnique(String nickname);

  /**
   * userId로 사용자 프로필 조회
   * @param userId
   * @return
   */
  GetUserProfileApiResponse getUserProfile(String userId, String targetUserId);

  /**
   * 자신의 프로필 상세 조회
   * @param userId
   * @return
   */
  GetMyProfileDetailsApiResponse getMyProfileDetails(String userId);
}
