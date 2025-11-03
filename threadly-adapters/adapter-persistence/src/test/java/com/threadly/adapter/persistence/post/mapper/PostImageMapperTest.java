package com.threadly.adapter.persistence.post.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.threadly.adapter.persistence.post.entity.PostImageEntity;
import com.threadly.core.domain.image.ImageStatus;
import com.threadly.core.domain.post.PostImage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * PostImageMapper 테스트
 */
class PostImageMapperTest {

  @DisplayName("PostImage Domain -> PostImageEntity")
  @Test
  void toEntity_shouldConvertDomainToEntity() {
    // given
    PostImage domain = new PostImage(
        "image-id-1",
        "stored-image.jpg",
        "https://example.com/image.jpg",
        1,
        ImageStatus.CONFIRMED
    );

    // when
    PostImageEntity entity = PostImageMapper.toEntity(domain);

    // then
    assertAll(
        () -> assertThat(entity.getPostImageId()).isEqualTo("image-id-1"),
        () -> assertThat(entity.getStoredFileName()).isEqualTo("stored-image.jpg"),
        () -> assertThat(entity.getImageOrder()).isEqualTo(1),
        () -> assertThat(entity.getImageUrl()).isEqualTo("https://example.com/image.jpg"),
        () -> assertThat(entity.getStatus()).isEqualTo(ImageStatus.CONFIRMED)
    );
  }

  @DisplayName("여러 이미지 순서로 PostImage를 PostImageEntity로 변환한다")
  @Test
  void toEntity_shouldConvertMultipleImages() {
    // given
    PostImage image1 = new PostImage(
        "image-1",
        "img1.jpg",
        "https://example.com/img1.jpg",
        0,
        ImageStatus.TEMPORARY
    );

    PostImage image2 = new PostImage(
        "image-2",
        "img2.jpg",
        "https://example.com/img2.jpg",
        1,
        ImageStatus.CONFIRMED
    );

    // when
    PostImageEntity entity1 = PostImageMapper.toEntity(image1);
    PostImageEntity entity2 = PostImageMapper.toEntity(image2);

    // then
    assertAll(
        () -> assertThat(entity1.getImageOrder()).isEqualTo(0),
        () -> assertThat(entity1.getStatus()).isEqualTo(ImageStatus.TEMPORARY),
        () -> assertThat(entity2.getImageOrder()).isEqualTo(1),
        () -> assertThat(entity2.getStatus()).isEqualTo(ImageStatus.CONFIRMED)
    );
  }
}
