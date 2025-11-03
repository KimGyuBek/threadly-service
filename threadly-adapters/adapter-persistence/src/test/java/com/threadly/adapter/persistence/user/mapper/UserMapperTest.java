package com.threadly.adapter.persistence.user.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.threadly.adapter.persistence.user.entity.UserEntity;
import com.threadly.core.domain.user.User;
import com.threadly.core.domain.user.UserRoleType;
import com.threadly.core.domain.user.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * UserMapper 테스트
 */
class UserMapperTest {

  @DisplayName("UserEntity -> User Domain")
  @Test
  void toDomain_shouldConvertEntityToDomain() {
    // given
    UserEntity entity = new UserEntity(
        "user-id-1",
        "테스트유저",
        "password123",
        "test@example.com",
        "010-1234-5678",
        UserRoleType.USER,
        UserStatus.ACTIVE,
        true,
        false
    );

    // when
    User domain = UserMapper.toDomain(entity);

    // then
    assertAll(
        () -> assertThat(domain.getUserId()).isEqualTo("user-id-1"),
        () -> assertThat(domain.getUserName()).isEqualTo("테스트유저"),
        () -> assertThat(domain.getPassword()).isEqualTo("password123"),
        () -> assertThat(domain.getEmail()).isEqualTo("test@example.com"),
        () -> assertThat(domain.getPhone()).isEqualTo("010-1234-5678"),
        () -> assertThat(domain.getUserRoleType()).isEqualTo(UserRoleType.USER),
        () -> assertThat(domain.getUserStatus()).isEqualTo(UserStatus.ACTIVE),
        () -> assertThat(domain.isEmailVerified()).isTrue(),
        () -> assertThat(domain.isPrivate()).isFalse()
    );
  }

  @DisplayName("User Domain -> UserEntity")
  @Test
  void toEntity_shouldConvertDomainToEntity() {
    // given
    User domain = User.builder()
        .userId("user-id-2")
        .userName("유저2")
        .password("pass456")
        .email("user2@example.com")
        .phone("010-9876-5432")
        .userRoleType(UserRoleType.ADMIN)
        .userStatus(UserStatus.INACTIVE)
        .isEmailVerified(false)
        .isPrivate(true)
        .build();

    // when
    UserEntity entity = UserMapper.toEntity(domain);

    // then
    assertAll(
        () -> assertThat(entity.getUserId()).isEqualTo("user-id-2"),
        () -> assertThat(entity.getUserName()).isEqualTo("유저2"),
        () -> assertThat(entity.getPassword()).isEqualTo("pass456"),
        () -> assertThat(entity.getEmail()).isEqualTo("user2@example.com"),
        () -> assertThat(entity.getPhone()).isEqualTo("010-9876-5432"),
        () -> assertThat(entity.getUserRoleType()).isEqualTo(UserRoleType.ADMIN),
        () -> assertThat(entity.getUserStatus()).isEqualTo(UserStatus.INACTIVE),
        () -> assertThat(entity.isEmailVerified()).isFalse(),
        () -> assertThat(entity.isPrivate()).isTrue()
    );
  }
}
