package com.threadly.core.port.user.out.profile.image;

import java.util.Optional;

/**
 * 프로필 이미지 조회 관련 port
 */
public interface UserProfileImageQueryPort {

  /**
   * 주어진 userProfileImageId에 해당하는 데이터가 있는지 조회
   *
   * @param userProfileImageId
   * @return
   */
  boolean existsNotDeletedByUserProfileImageId(String userProfileImageId);

  /**
   * 주어진 userId에 해당하는 CONFIRMED 상태의 이미지 id 조회
   *
   * @param userId
   * @return
   */
  Optional<String> findConfirmedProfileImageIdByUserId(String userId);


}
