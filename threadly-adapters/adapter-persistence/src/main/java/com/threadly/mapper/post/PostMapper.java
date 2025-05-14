package com.threadly.mapper.post;

import com.threadly.entity.post.PostEntity;
import com.threadly.entity.user.UserEntity;
import com.threadly.posts.Post;

/**
 * Post Mapper
 */
public class PostMapper {

  /**
   * PostEntity -> PostDomain
   *
   * @param entity
   * @return
   */
  public static Post toDomain(PostEntity entity) {
    if (entity == null) {
      throw new IllegalArgumentException("PostEntity cannot be null");
    }
    String userId = (entity.getUser() != null) ? entity.getUser().getUserId() : null;
    return new Post(
        entity.getPostId(),
        entity.getContent(),
        userId,
        entity.getViewCount(),
        entity.getModifiedAt()
    );
  }

  /**
   * PostDomain -> PostEntity
   * @param post
   * @return
   */
  public static PostEntity toEntity(Post post, UserEntity userEntity) {
    return new PostEntity(
        post.getPostId(),
        userEntity,
        post.getContent(),
        post.getViewCount()
    );
  }
}
