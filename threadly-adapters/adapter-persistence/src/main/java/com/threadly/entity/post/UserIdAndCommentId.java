package com.threadly.entity.post;


import jakarta.persistence.Embeddable;

/**
 * comment_likes 복합키
 */
@Embeddable
public class UserIdAndCommentId {

  private String commentId;
  private String userId;

  public UserIdAndCommentId() {
  }

  public UserIdAndCommentId(String commentId, String userId) {
    this.commentId = commentId;
    this.userId = userId;
  }

}
