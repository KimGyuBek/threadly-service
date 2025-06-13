package com.threadly.mapper.post;

import com.threadly.entity.post.PostEntity;
import com.threadly.entity.post.PostImageEntity;
import com.threadly.post.PostImage;

/**
 * Post Mapper
 */
public class PostImageMapper {

//  /**
//   * Post -> PostDomain
//   *
//   * @param entity
//   * @return
//   */
//  public static Post toDomain(PostEntity entity) {
//    if (entity == null) {
//      throw new IllegalArgumentException("PostEntity cannot be null");
//    }
//    String userId = (entity.getUser() != null) ? entity.getUser().getUserId() : null;
//    return new Post(
//        entity.getPostId(),
//        userId,
//        entity.getContent(),
//        entity.getViewCount(),
//        entity.getStatus(),
//        entity.getModifiedAt()
//    );
//  }

  /**
   * PostImage Domaie -> PostImage Entity
   *
   * @param domain
   * @return
   */
  public static PostImageEntity toEntity(PostImage domain) {
    return new PostImageEntity(
        domain.getPostImageId(),
        PostEntity.fromId(domain.getPostId()),
        domain.getStoredName(),
        domain.getImageOrder(),
        domain.getImageUrl(),
        null,
        null
    );
  }
}



