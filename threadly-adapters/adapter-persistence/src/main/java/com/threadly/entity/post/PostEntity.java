package com.threadly.entity.post;

import com.threadly.entity.BaseEntity;
import com.threadly.entity.user.UserEntity;
import com.threadly.util.RandomUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

  /*수정 시간 조회*/
  public LocalDateTime getModifiedAt() {
    return super.getModifiedAt();
  }

  public static PostEntity newPost(UserEntity user, String content) {
    return new PostEntity(
        RandomUtils.generateUUID(),
        user,
        content,
        0
    );
  }

  public PostEntity() {
  }
}
