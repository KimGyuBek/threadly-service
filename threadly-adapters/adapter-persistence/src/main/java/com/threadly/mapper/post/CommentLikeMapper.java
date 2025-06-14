package com.threadly.mapper.post;

import com.threadly.entity.post.CommentLikeEntity;
import com.threadly.entity.post.PostCommentEntity;
import com.threadly.entity.post.UserIdAndCommentId;
import com.threadly.entity.user.UserEntity;
import com.threadly.post.comment.CommentLike;

/**
 * Post Comment Like Mapper
 */
public class CommentLikeMapper {

  /**
   * entity -> domain
   *
   * @param entity
   * @return
   */
  public static CommentLike toDomain(CommentLikeEntity entity) {
    return new CommentLike(
        entity.getId().getCommentId(),
        entity.getId().getUserId()
    );
  }

  /**
   * domain -> entity
   *
   * @param domain
   * @return
   */
  public static CommentLikeEntity toEntity(CommentLike domain) {
    return new CommentLikeEntity(
        new UserIdAndCommentId(domain.getCommentId(), domain.getUserId()),
        PostCommentEntity.fromId(domain.getCommentId()),
        UserEntity.fromId(domain.getUserId()),
        null
    );

  }

}
