package com.threadly.adapter.persistence.follow.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.threadly.adapter.persistence.follow.entity.FollowEntity;
import com.threadly.adapter.persistence.user.entity.UserEntity;
import com.threadly.core.domain.follow.Follow;
import com.threadly.core.domain.follow.FollowStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * FollowMapper 테스트
 */
class FollowMapperTest {

  @DisplayName("Follow Domain -> FollowEntity")
  @Test
  void toEntity_shouldConvertDomainToEntity() {
    // given
    Follow domain = Follow.builder()
        .followId("follow-id-1")
        .followerId("follower-id-1")
        .followingId("following-id-1")
        .statusType(FollowStatus.APPROVED)
        .build();

    // when
    FollowEntity entity = FollowMapper.toEntity(domain);

    // then
    assertAll(
        () -> assertThat(entity.getFollowId()).isEqualTo("follow-id-1"),
        () -> assertThat(entity.getFollower().getUserId()).isEqualTo("follower-id-1"),
        () -> assertThat(entity.getFollowing().getUserId()).isEqualTo("following-id-1"),
        () -> assertThat(entity.getStatusType()).isEqualTo(FollowStatus.APPROVED)
    );
  }

  @DisplayName("FollowEntity -> Follow Domain")
  @Test
  void toDomain_shouldConvertEntityToDomain() {
    // given
    UserEntity follower = UserEntity.fromId("follower-id-2");
    UserEntity following = UserEntity.fromId("following-id-2");

    FollowEntity entity = new FollowEntity(
        "follow-id-2",
        follower,
        following,
        FollowStatus.PENDING
    );

    // when
    Follow domain = FollowMapper.toDomain(entity);

    // then
    assertAll(
        () -> assertThat(domain.getFollowId()).isEqualTo("follow-id-2"),
        () -> assertThat(domain.getFollowerId()).isEqualTo("follower-id-2"),
        () -> assertThat(domain.getFollowingId()).isEqualTo("following-id-2"),
        () -> assertThat(domain.getStatusType()).isEqualTo(FollowStatus.PENDING)
    );
  }
}
