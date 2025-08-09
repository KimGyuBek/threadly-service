package com.threadly.core.domain.user.profile.image;

import com.threadly.core.domain.image.ImageStatus;
import com.threadly.commons.utils.RandomUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 사용자 프로필 이미지 도메인
 */
@Getter
@AllArgsConstructor
@Builder
public class UserProfileImage {

  private String userProfileImageId;
  private String storedFileName;
  private String imageUrl;
  private String userId;
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
        null,
        ImageStatus.TEMPORARY
    );
  }

  /**
   * 주어진 userProfileImageId를 새로운 프로필 이미지로 설정
   *
   * @param userProfileImageId
   */
  public void setProfileImage(String userProfileImageId) {
    this.userProfileImageId = userProfileImageId;
    this.status = ImageStatus.CONFIRMED;
  }

  @Override
  public String toString() {
    return "UserProfileImage{" +
        "userProfileImageId='" + userProfileImageId + '\'' +
        ", storedFileName='" + storedFileName + '\'' +
        ", imageUrl='" + imageUrl + '\'' +
        ", userId='" + userId + '\'' +
        ", status=" + status +
        '}';
  }
}
