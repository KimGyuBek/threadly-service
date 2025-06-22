package com.threadly.entity.post;

import com.threadly.entity.BaseEntity;
import com.threadly.post.PostImageStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 게시글 이미지 entity
 */
@Table(name = "post_images")
@EntityListeners(AuditingEntityListener.class)
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class PostImageEntity extends BaseEntity {

  @Id
  @Column(name = "post_image_id")
  private String postImageId;

  @JoinColumn(name = "post_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private PostEntity post;

  @Column(name = "stored_file_name", nullable = false)
  private String storedFileName;

  @Column(name = "image_order", nullable = false)
  private int imageOrder = 0;

  @Column(name = "image_url", nullable = false)
  private String imageUrl = "/";

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private PostImageStatus status;

}