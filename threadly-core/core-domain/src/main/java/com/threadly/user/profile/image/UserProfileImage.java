package com.threadly.user.profile.image;

import com.threadly.image.ImageStatus;
import com.threadly.utils.RandomUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 사용자 프로필 이미지 도메인
 */
@Getter
@AllArgsConstructor
public class UserProfileImage {

  private String userProfileImageId;
  private String storedFileName;
  private String imageUrl;
  private ImageStatus status;

  /**
   * 새프로필 이미지 생성
   *
   * @param storedFileName
   * @param imageUrl
   * @return
   */
  public static UserProfileImage newProfileImage(String storedFileName, String imageUrl) {
    return new UserProfileImage(
        RandomUtils.generateNanoId(),
        storedFileName,
        imageUrl,
        ImageStatus.TEMPORARY
    );
  }

  @Override
  public String toString() {
    return "UserProfileImage{" +
        "userProfileImageId='" + userProfileImageId + '\'' +
        ", storedFileName='" + storedFileName + '\'' +
        ", imageUrl='" + imageUrl + '\'' +
        ", status=" + status +
        '}';
  }
}
