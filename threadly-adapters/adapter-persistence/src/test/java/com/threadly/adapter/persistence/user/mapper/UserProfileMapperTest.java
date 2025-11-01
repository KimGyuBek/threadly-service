package com.threadly.adapter.persistence.user.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.threadly.adapter.persistence.user.entity.UserProfileEntity;
import com.threadly.core.domain.user.UserGenderType;
import com.threadly.core.domain.user.profile.UserProfile;
import com.threadly.core.domain.user.profile.UserProfileType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * UserProfileMapper 테스트
 */
class UserProfileMapperTest {

  @DisplayName("UserProfileEntity -> UserProfile Domain")
  @Test
  void toDomain_shouldConvertEntityToDomain() {
    // given
    UserProfileEntity entity = UserProfileEntity.newUserProfile(
        "user-id-1",
        "테스트닉네임",
        "안녕하세요",
        "테스트 바이오",
        UserGenderType.MALE,
        UserProfileType.USER
    );

    // when
    UserProfile domain = UserProfileMapper.toDomain(entity);

    // then
    assertAll(
        () -> assertThat(domain.getUserId()).isEqualTo("user-id-1"),
        () -> assertThat(domain.getNickname()).isEqualTo("테스트닉네임"),
        () -> assertThat(domain.getStatusMessage()).isEqualTo("안녕하세요"),
        () -> assertThat(domain.getBio()).isEqualTo("테스트 바이오"),
        () -> assertThat(domain.getGenderType()).isEqualTo(UserGenderType.MALE),
        () -> assertThat(domain.getUserProfileType()).isEqualTo(UserProfileType.USER)
    );
  }

  @DisplayName("UserProfile Domain -> UserProfileEntity")
  @Test
  void toEntity_shouldConvertDomainToEntity() {
    // given
    UserProfile domain = UserProfile.builder()
        .userId("user-id-2")
        .nickname("유저2")
        .statusMessage("상태메시지")
        .bio("바이오")
        .genderType(UserGenderType.FEMALE)
        .userProfileType(UserProfileType.PROFESSIONAL)
        .build();

    // when
    UserProfileEntity entity = UserProfileMapper.toEntity(domain);

    // then
    assertAll(
        () -> assertThat(entity.getUserId()).isEqualTo("user-id-2"),
        () -> assertThat(entity.getNickname()).isEqualTo("유저2"),
        () -> assertThat(entity.getStatusMessage()).isEqualTo("상태메시지"),
        () -> assertThat(entity.getBio()).isEqualTo("바이오"),
        () -> assertThat(entity.getGender()).isEqualTo(UserGenderType.FEMALE),
        () -> assertThat(entity.getProfileType()).isEqualTo(UserProfileType.PROFESSIONAL)
    );
  }
}
