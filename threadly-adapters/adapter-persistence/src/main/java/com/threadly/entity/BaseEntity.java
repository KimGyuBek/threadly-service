package com.threadly.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class BaseEntity {

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "modified_at")
  private LocalDateTime modifiedAt;

}
