package com.threadly.entity.post;

import com.threadly.entity.BaseEntity;
import com.threadly.entity.user.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * 게시글 Entity
 */
@Entity
@Table(name = "posts")
public class PostEntity extends BaseEntity {

  @Id
  @Column(name = "post_id")
  private String postsId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private UserEntity user;

  @Column(name = "content")
  private String content;

  @Column(name = "view_count")
  private int viewCount;

}
