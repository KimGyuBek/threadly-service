package com.threadly.adapter.persistence.post.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.threadly.adapter.persistence.post.entity.PostEntity;
import com.threadly.adapter.persistence.post.entity.PostIdAndUserId;
import com.threadly.adapter.persistence.post.entity.PostLikeEntity;
import com.threadly.adapter.persistence.user.entity.UserEntity;
import com.threadly.core.domain.post.PostLike;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * PostLikeMapper 테스트
 */
class PostLikeMapperTest {

  @DisplayName("PostLikeEntity -> PostLike Domain")
  @Test
  void toDomain_shouldConvertEntityToDomain() {
    // given
    PostIdAndUserId id = new PostIdAndUserId("post-id-1", "user-id-1");
    PostEntity post = PostEntity.fromId("post-id-1");
    UserEntity user = UserEntity.fromId("user-id-1");

    PostLikeEntity entity = new PostLikeEntity(id, post, user, null);

    // when
    PostLike domain = PostLikeMapper.toDomain(entity);

    // then
    assertAll(
        () -> assertThat(domain.getPostId()).isEqualTo("post-id-1"),
        () -> assertThat(domain.getUserId()).isEqualTo("user-id-1")
    );
  }

  @DisplayName("PostLike Domain -> PostLikeEntity")
  @Test
  void toEntity_shouldConvertDomainToEntity() {
    // given
    PostLike domain = new PostLike("post-id-2", "user-id-2");

    // when
    PostLikeEntity entity = PostLikeMapper.toEntity(domain);

    // then
    assertAll(
        () -> assertThat(entity.getId().getPostId()).isEqualTo("post-id-2"),
        () -> assertThat(entity.getId().getUserId()).isEqualTo("user-id-2"),
        () -> assertThat(entity.getPost().getPostId()).isEqualTo("post-id-2"),
        () -> assertThat(entity.getUser().getUserId()).isEqualTo("user-id-2")
    );
  }
}
