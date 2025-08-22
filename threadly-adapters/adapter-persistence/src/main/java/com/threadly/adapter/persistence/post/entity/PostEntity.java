package com.threadly.adapter.persistence.post.entity;

import com.threadly.adapter.persistence.base.BaseEntity;
import com.threadly.adapter.persistence.user.entity.UserEntity;
import com.threadly.core.domain.post.PostStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 게시글 Entity
 */
@Entity
@Getter
@Table(name = "posts")
@AllArgsConstructor
public class PostEntity extends BaseEntity {

  @Id
  @Column(name = "post_id")
  private String postId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private UserEntity user;

  @Column(name = "content")
  private String content;

  @Column(name = "view_count")
  private int viewCount;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private PostStatus status;

  /*수정 시간 조회*/
  public LocalDateTime getModifiedAt() {
    return super.getModifiedAt();
  }

  /**
   * 매핑 전용
   * @param postId
   * @return
   */
  public static PostEntity fromId(String postId) {
    PostEntity postEntity = new PostEntity();
    postEntity.postId = postId;

    return postEntity;
  }

  public PostEntity() {
  }
}
