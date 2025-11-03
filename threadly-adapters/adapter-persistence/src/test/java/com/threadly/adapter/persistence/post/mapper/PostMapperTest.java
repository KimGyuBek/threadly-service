package com.threadly.adapter.persistence.post.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.threadly.adapter.persistence.post.entity.PostEntity;
import com.threadly.adapter.persistence.user.entity.UserEntity;
import com.threadly.core.domain.post.Post;
import com.threadly.core.domain.post.PostStatus;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * PostMapper 테스트
 */
class PostMapperTest {

  @DisplayName("PostEntity -> Post Domain")
  @Test
  void toDomain_shouldConvertEntityToDomain() {
    // given
    UserEntity user = UserEntity.fromId("user-id-1");
    LocalDateTime modifiedAt = LocalDateTime.now();

    PostEntity entity = new PostEntity(
        "post-id-1",
        user,
        "테스트 게시글 내용",
        100,
        PostStatus.ACTIVE
    );

    // when
    Post domain = PostMapper.toDomain(entity);

    // then
    assertAll(
        () -> assertThat(domain.getPostId()).isEqualTo("post-id-1"),
        () -> assertThat(domain.getUserId()).isEqualTo("user-id-1"),
        () -> assertThat(domain.getContent()).isEqualTo("테스트 게시글 내용"),
        () -> assertThat(domain.getViewCount()).isEqualTo(100),
        () -> assertThat(domain.getStatus()).isEqualTo(PostStatus.ACTIVE)
    );
  }

  @DisplayName("PostEntity가 null이면 예외를 던진다")
  @Test
  void toDomain_shouldThrowException_whenEntityIsNull() {
    // given
    PostEntity entity = null;

    // when & then
    assertThatThrownBy(() -> PostMapper.toDomain(entity))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("PostEntity cannot be null");
  }

  @DisplayName("Post Domain -> PostEntity")
  @Test
  void toEntity_shouldConvertDomainToEntity() {
    // given
    LocalDateTime modifiedAt = LocalDateTime.now();
    Post domain = new Post(
        "post-id-2",
        "user-id-2",
        "게시글 내용2",
        50,
        PostStatus.DELETED,
        modifiedAt
    );

    // when
    PostEntity entity = PostMapper.toEntity(domain);

    // then
    assertAll(
        () -> assertThat(entity.getPostId()).isEqualTo("post-id-2"),
        () -> assertThat(entity.getUser().getUserId()).isEqualTo("user-id-2"),
        () -> assertThat(entity.getContent()).isEqualTo("게시글 내용2"),
        () -> assertThat(entity.getViewCount()).isEqualTo(50),
        () -> assertThat(entity.getStatus()).isEqualTo(PostStatus.DELETED)
    );
  }
}
