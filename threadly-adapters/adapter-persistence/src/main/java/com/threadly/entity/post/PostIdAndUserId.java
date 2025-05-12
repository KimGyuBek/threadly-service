package com.threadly.entity.post;


import jakarta.persistence.Embeddable;

/**
 * post_likes 복합키
 */
@Embeddable
public class PostIdAndUserId {

  private String postId;
  private String userId;

  public PostIdAndUserId() {
  }

  public PostIdAndUserId(String postId, String userId) {
    this.postId = postId;
    this.userId = userId;
  }

}
