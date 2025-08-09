package com.threadly.base;

import com.threadly.image.ImageStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Base Image Entity
 */
@MappedSuperclass
@NoArgsConstructor()
@Getter
public abstract class BaseImageEntity extends BaseEntity {

  @Column(name = "stored_file_name", nullable = false)
  private String storedFileName;

  @Column(name = "image_url", nullable = false)
  private String imageUrl;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private ImageStatus status;

  public BaseImageEntity(String storedFileName, String imageUrl, ImageStatus status) {
    this.storedFileName = storedFileName;
    this.imageUrl = imageUrl;
    this.status = status;
  }
}
