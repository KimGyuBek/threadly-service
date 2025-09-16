package com.threadly.core.port.user.out.profile;

import com.threadly.core.port.user.out.profile.projection.MyProfileDetailsProjection;
import com.threadly.core.port.user.out.profile.projection.UserPreviewProjection;
import com.threadly.core.port.user.out.profile.projection.UserProfileProjection;
import java.util.Optional;

public interface UserProfileQueryPort {

  void findByUserId(String userId);

  /**
   * userId에 해당하는 user comment preview 프로젝션 조회
   *
   * @param userId
   * @return
   */
  UserPreviewProjection findUserPreviewByUserId(String userId);

  /**
   * userId에 해당하는 profile 존재 유무
   *
   * @param userId
   * @return
   */
  boolean existsUserProfileByUserId(String userId);

  /**
   * nickname 중복 검증
   *
   * @param nickname
   * @return
   */
  boolean existsByNickname(String nickname);

  /**
   * userId에 해당하는 userProfile 정보 조회
   *
   * @param userId
   * @return
   */
  Optional<UserProfileProjection> findUserProfileByUserId(String userId);

  /**
   * 내 프로필 정보 상세 조회
   * @param userId
   * @return
   */
  Optional<MyProfileDetailsProjection> findMyProfileDetailsByUserId(String userId);

}
