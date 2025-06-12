package com.threadly.entity.post;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 게시글 이미지 entity
 */
@Table(name = "post_images")
@EntityListeners(AuditingEntityListener.class)
@Entity
public class PostImageEntity {

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

  @CreatedDate
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

}



/*

post_image_id | post_id | storedFileName | createdAt
pi_01         | pos01   | stored_01 | 1234-1234-1244
pi_02         | pos01   | stored_02 | 1234-1234-1234
pi_03         | pos01   | stored_03 | 1241-1234-1234
pi_04         | pos02   | stored_05 | 1241-2134-1234

 */
