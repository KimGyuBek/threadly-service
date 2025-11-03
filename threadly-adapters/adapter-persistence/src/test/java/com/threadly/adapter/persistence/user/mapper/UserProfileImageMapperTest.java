package com.threadly.adapter.persistence.user.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.threadly.adapter.persistence.user.entity.UserProfileImageEntity;
import com.threadly.core.domain.image.ImageStatus;
import com.threadly.core.domain.user.profile.image.UserProfileImage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * UserProfileImageMapper 테스트
 */
class UserProfileImageMapperTest {

  @DisplayName("UserProfileImage Domain -> UserProfileImageEntity")
  @Test
  void toEntity_shouldConvertDomainToEntity() {
    // given
    UserProfileImage domain = UserProfileImage.builder()
        .userProfileImageId("image-id-1")
        .storedFileName("stored-file-name.jpg")
        .imageUrl("https://example.com/image.jpg")
        .status(ImageStatus.CONFIRMED)
        .build();

    // when
    UserProfileImageEntity entity = UserProfileImageMapper.toEntity(domain);

    // then
    assertAll(
        () -> assertThat(entity.getUserProfileImageId()).isEqualTo("image-id-1"),
        () -> assertThat(entity.getStoredFileName()).isEqualTo("stored-file-name.jpg"),
        () -> assertThat(entity.getImageUrl()).isEqualTo("https://example.com/image.jpg"),
        () -> assertThat(entity.getStatus()).isEqualTo(ImageStatus.CONFIRMED)
    );
  }

  @DisplayName("UserProfileImageEntity -> UserProfileImage Domain")
  @Test
  void toDomain_shouldConvertEntityToDomain() {
    // given
    UserProfileImageEntity entity = UserProfileImageEntity.newUserProfileImage(
        "image-id-2",
        null,
        "file2.jpg",
        "https://example.com/file2.jpg",
        ImageStatus.TEMPORARY
    );

    // when
    UserProfileImage domain = UserProfileImageMapper.toDomain(entity);

    // then
    assertThat(domain.getUserProfileImageId()).isEqualTo("image-id-2");
  }
}
