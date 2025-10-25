package com.threadly.core.port.user.in.profile.image;

import com.threadly.core.port.user.in.profile.image.dto.SetMyProfileImageCommand;
import com.threadly.core.port.user.in.profile.image.dto.UploadMyProfileImageApiResponse;

/**
 * 내 프로필 이미지 command 관련 usecase
 */
public interface MyProfileImageCommandUseCase {

  /**
   * 주어진 userId, profileImageId로 프로필 이미지 업데이트 또는 삭제
   * @param userId
   * @param profileImageId
   */
  void updateProfileImage(String userId, String profileImageId);

  /**
   * 내 프로필 이미지 설정
   *
   * @param command
   * @return
   */
  UploadMyProfileImageApiResponse setMyProfileImage(SetMyProfileImageCommand command);
}
