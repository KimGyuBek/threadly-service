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
 * 게시글 댓글 Entity
 */
@Table(name = "post_comments")
@Entity
public class PostCommentEntity extends BaseEntity {

  @Id
  @Column(name = "comment_id")
  private String commentId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id")
  private PostEntity post;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private UserEntity user;

  @Column(name = "content")
  private String content;
}
