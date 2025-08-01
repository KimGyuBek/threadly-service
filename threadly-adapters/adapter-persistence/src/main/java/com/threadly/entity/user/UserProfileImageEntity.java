package com.threadly.entity.user;

import com.threadly.entity.image.BaseImageEntity;
import com.threadly.image.ImageStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 사용자 프로필 이미지 entity
 */
@Table(name = "user_profile_images")
@EntityListeners(AuditingEntityListener.class)
@Entity
@Getter
@NoArgsConstructor
public class UserProfileImageEntity extends BaseImageEntity {

  @Id
  @Column(name = "user_profile_image_id")
  String userProfileImageId;

  @JoinColumn(name = "user_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private UserProfileEntity userProfile;

  public UserProfileImageEntity(String userProfileImageId, UserProfileEntity userProfile,
      String storedFileName, String imageUrl, ImageStatus status) {
    super(storedFileName, imageUrl, status);
    this.userProfileImageId = userProfileImageId;
    this.userProfile = userProfile;
  }

  /**
   * Entity 생성
   *
   * @param userProfileImageId
   * @param userProfile
   * @param storedFileName
   * @param imageUrl
   * @param status
   * @return
   */
  public static UserProfileImageEntity newUserProfileImage(String userProfileImageId,
      UserProfileEntity userProfile,
      String storedFileName, String imageUrl, ImageStatus status) {
    return new UserProfileImageEntity(userProfileImageId,
        userProfile,
        storedFileName,
        imageUrl,
        status);

  }
}
