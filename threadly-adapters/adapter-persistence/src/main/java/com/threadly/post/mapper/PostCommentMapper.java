package com.threadly.post.mapper;

import com.threadly.post.entity.PostCommentEntity;
import com.threadly.post.entity.PostEntity;
import com.threadly.user.entity.UserEntity;
import com.threadly.post.comment.PostComment;

/**
 * Post Comment Mapper
 */
public class PostCommentMapper {

  /**
   * PostCommentEntity -> PostCommentDomain
   *
   * @param entity
   * @return
   */
  public static PostComment toDomain(PostCommentEntity entity) {
    if (entity == null) {
      throw new IllegalArgumentException("PostCommentEntity cannot be null");
    }
    return new PostComment(
        entity.getCommentId(),
        entity.getPost().getPostId(),
        entity.getUser().getUserId(),
        entity.getContent(),
        entity.getStatus(),
        entity.getCreatedAt()
    );
  }

  /**
   * PostCommentDomain -> PostCommentEntity
   *
   * @param postComment
   * @return
   */
  public static PostCommentEntity toEntity(PostComment postComment) {
    return new PostCommentEntity(
        postComment.getCommentId(),
        PostEntity.fromId(postComment.getPostId()),
        UserEntity.fromId(postComment.getUserId()),
        postComment.getContent(),
        postComment.getStatus()
    );
  }
}



