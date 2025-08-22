package com.threadly.adapter.persistence.post.mapper;

import com.threadly.adapter.persistence.post.entity.PostImageEntity;
import com.threadly.core.domain.post.PostImage;

/**
 * Post Mapper
 */
public class PostImageMapper {

  /**
   * PostImage Domaie -> PostImage Entity
   *
   * @param domain
   * @return
   */
  public static PostImageEntity toEntity(PostImage domain) {
    return new PostImageEntity(
        domain.getPostImageId(),
        null,
        domain.getStoredName(),
        domain.getImageOrder(),
        domain.getImageUrl(),
        domain.getStatus()
    );
  }
}



