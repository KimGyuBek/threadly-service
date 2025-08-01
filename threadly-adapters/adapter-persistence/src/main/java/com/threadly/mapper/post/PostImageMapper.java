package com.threadly.mapper.post;

import com.threadly.entity.post.PostEntity;
import com.threadly.entity.post.PostImageEntity;
import com.threadly.post.PostImage;

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



