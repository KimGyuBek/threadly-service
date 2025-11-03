package com.threadly.adapter.persistence.post.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.threadly.adapter.persistence.post.entity.PostCommentEntity;
import com.threadly.adapter.persistence.post.entity.PostEntity;
import com.threadly.adapter.persistence.user.entity.UserEntity;
import com.threadly.core.domain.post.PostCommentStatus;
import com.threadly.core.domain.post.comment.PostComment;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * PostCommentMapper 테스트
 */
class PostCommentMapperTest {

  @DisplayName("PostCommentEntity -> PostComment Domain")
  @Test
  void toDomain_shouldConvertEntityToDomain() {
    // given
    PostEntity post = PostEntity.fromId("post-id-1");
    UserEntity user = UserEntity.fromId("user-id-1");
    LocalDateTime createdAt = LocalDateTime.now();

    PostCommentEntity entity = new PostCommentEntity(
        "comment-id-1",
        post,
        user,
        "댓글 내용",
        PostCommentStatus.ACTIVE
    );

    // when
    PostComment domain = PostCommentMapper.toDomain(entity);

    // then
    assertAll(
        () -> assertThat(domain.getCommentId()).isEqualTo("comment-id-1"),
        () -> assertThat(domain.getPostId()).isEqualTo("post-id-1"),
        () -> assertThat(domain.getUserId()).isEqualTo("user-id-1"),
        () -> assertThat(domain.getContent()).isEqualTo("댓글 내용"),
        () -> assertThat(domain.getStatus()).isEqualTo(PostCommentStatus.ACTIVE)
    );
  }

  @DisplayName("PostCommentEntity가 null이면 예외를 던진다")
  @Test
  void toDomain_shouldThrowException_whenEntityIsNull() {
    // given
    PostCommentEntity entity = null;

    // when & then
    assertThatThrownBy(() -> PostCommentMapper.toDomain(entity))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("PostCommentEntity cannot be null");
  }

  @DisplayName("PostComment Domain -> PostCommentEntity")
  @Test
  void toEntity_shouldConvertDomainToEntity() {
    // given
    LocalDateTime createdAt = LocalDateTime.now();
    PostComment domain = new PostComment(
        "comment-id-2",
        "post-id-2",
        "user-id-2",
        "댓글 내용2",
        PostCommentStatus.DELETED,
        createdAt
    );

    // when
    PostCommentEntity entity = PostCommentMapper.toEntity(domain);

    // then
    assertAll(
        () -> assertThat(entity.getCommentId()).isEqualTo("comment-id-2"),
        () -> assertThat(entity.getPost().getPostId()).isEqualTo("post-id-2"),
        () -> assertThat(entity.getUser().getUserId()).isEqualTo("user-id-2"),
        () -> assertThat(entity.getContent()).isEqualTo("댓글 내용2"),
        () -> assertThat(entity.getStatus()).isEqualTo(PostCommentStatus.DELETED)
    );
  }
}
