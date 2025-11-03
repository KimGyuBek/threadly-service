package com.threadly.adapter.persistence.post.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.threadly.adapter.persistence.post.entity.CommentLikeEntity;
import com.threadly.adapter.persistence.post.entity.PostCommentEntity;
import com.threadly.adapter.persistence.post.entity.UserIdAndCommentId;
import com.threadly.adapter.persistence.user.entity.UserEntity;
import com.threadly.core.domain.post.comment.CommentLike;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * CommentLikeMapper 테스트
 */
class CommentLikeMapperTest {

  @DisplayName("CommentLikeEntity -> CommentLike Domain")
  @Test
  void toDomain_shouldConvertEntityToDomain() {
    // given
    UserIdAndCommentId id = new UserIdAndCommentId("comment-id-1", "user-id-1");
    PostCommentEntity comment = PostCommentEntity.fromId("comment-id-1");
    UserEntity user = UserEntity.fromId("user-id-1");

    CommentLikeEntity entity = new CommentLikeEntity(id, comment, user, null);

    // when
    CommentLike domain = CommentLikeMapper.toDomain(entity);

    // then
    assertAll(
        () -> assertThat(domain.getCommentId()).isEqualTo("comment-id-1"),
        () -> assertThat(domain.getUserId()).isEqualTo("user-id-1")
    );
  }

  @DisplayName("CommentLike Domain -> CommentLikeEntity")
  @Test
  void toEntity_shouldConvertDomainToEntity() {
    // given
    CommentLike domain = CommentLike.builder()
        .commentId("comment-id-2")
        .userId("user-id-2")
        .build();

    // when
    CommentLikeEntity entity = CommentLikeMapper.toEntity(domain);

    // then
    assertAll(
        () -> assertThat(entity.getId().getCommentId()).isEqualTo("comment-id-2"),
        () -> assertThat(entity.getId().getUserId()).isEqualTo("user-id-2"),
        () -> assertThat(entity.getComment().getCommentId()).isEqualTo("comment-id-2"),
        () -> assertThat(entity.getUser().getUserId()).isEqualTo("user-id-2")
    );
  }
}
