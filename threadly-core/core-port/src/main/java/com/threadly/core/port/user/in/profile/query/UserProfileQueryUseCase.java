package com.threadly.core.port.user.in.profile.query;

import com.threadly.core.port.user.in.profile.query.dto.GetMyProfileDetailsApiResponse;
import com.threadly.core.port.user.in.profile.query.dto.GetUserProfileApiResponse;

/**
 * 사용자 프로필 command usecase
 */
public interface UserProfileQueryUseCase {

  /**
   * 내 프로필 상세 조회
   *
   * @param userId
   * @return
   */
  GetMyProfileDetailsApiResponse getMyProfileDetails(String userId);

  /**
   * userId에 해당하는 profile 존재 여부 조회
   *
   * @param userId
   * @return
   */
  boolean existsUserProfile(String userId);

  /**
   * nickname 중복 검증
   *
   * @param nickname
   */
  void validateNicknameUnique(String nickname);

  /**
   * userId로 사용자 프로필 조회
   *
   * @param userId
   * @return
   */
  GetUserProfileApiResponse getUserProfile(String userId, String targetUserId);

}
