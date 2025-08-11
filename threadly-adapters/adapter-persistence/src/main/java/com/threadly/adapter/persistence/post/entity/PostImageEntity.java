package com.threadly.adapter.persistence.post.entity;

import com.threadly.adapter.persistence.base.BaseImageEntity;
import com.threadly.core.domain.image.ImageStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 게시글 이미지 entity
 */
@Table(name = "post_images")
@EntityListeners(AuditingEntityListener.class)
@Entity
@Getter
@NoArgsConstructor
public class PostImageEntity extends BaseImageEntity {

  @Id
  @Column(name = "post_image_id")
  private String postImageId;

  @JoinColumn(name = "post_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private PostEntity post;

  @Column(name = "image_order", nullable = false)
  private int imageOrder = 0;

  public PostImageEntity(String postImageId, PostEntity post, String storedFileName, int imageOrder,
      String imageUrl, ImageStatus status) {
    super(storedFileName, imageUrl, status);
    this.postImageId = postImageId;
    this.post = post;
    this.imageOrder = imageOrder;
  }
}