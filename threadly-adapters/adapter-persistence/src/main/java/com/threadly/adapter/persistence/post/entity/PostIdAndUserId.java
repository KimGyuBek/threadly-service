package com.threadly.adapter.persistence.post.entity;


import jakarta.persistence.Embeddable;
import lombok.Getter;

/**
 * post_likes 복합키
 */
@Embeddable
@Getter
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
