package com.threadly.core.domain.post;

import com.google.common.annotations.VisibleForTesting;
import com.threadly.core.domain.post.comment.PostComment;
import com.threadly.commons.utils.RandomUtils;
import java.time.LocalDateTime;
import lombok.Getter;

/**
 * Post 도메인
 */
@Getter
public class Post {

  private String postId;
  private String userId;
  private String content;

  private int viewCount;

  private PostStatus status;

  private LocalDateTime postedAt;

  public Post(String postId, String userId, String content, int viewCount, PostStatus status,
      LocalDateTime postedAt) {
    this.postId = postId;
    this.content = content;
    this.userId = userId;
    this.viewCount = viewCount;
    this.postedAt = postedAt;
    this.status = status;
  }

  /**
   * 새로운 게시글 생성
   *
   * @param userId
   * @param content
   * @return
   */
  public static Post newPost(String userId, String content) {
    return new Post(
        RandomUtils.generateNanoId(),
        userId,
        (content != null) ? content : "",
        0,
        PostStatus.ACTIVE,
        LocalDateTime.now()
    );
  }

  /**
   * content 수정
   *
   * @param content
   * @return
   */
  public Post updateContent(String content) {
    this.content = (content != null) ? content : "";
    return this;
  }


  /**
   * 조회수 증가
   */
  public void increaseViewCount() {
    this.viewCount++;
  }


  /**
   * 게시글이 좋아요 가능한 상태인지 검증
   *
   * @return
   */
  public void validateLikable() {
    if (status != PostStatus.ACTIVE) {
      throw new CannotLikePostException();
    }
  }

  /**
   * 좋아요 생성
   *
   * @param userId
   * @return
   */
  public PostLike addLike(String userId) {
    return
        PostLike.newLike(this.postId, userId);
  }


  /**
   * 게시글 삭제 상태로 변경
   */
  public void markAsDeleted() {
    this.status = PostStatus.DELETED;
  }

  /**
   * 게시글 블라인드 상태로 변경
   */
  public void markAsBlocked() {
    this.status = PostStatus.BLOCKED;
  }

  /**
   * 게시글 블라인드 해제 상태로 변경
   */
  public void markAsUnblocked() {
    this.status = PostStatus.ACTIVE;
  }

  /**
   * 게시글 아카이브 상태로 변경
   */
  public void markAsArchived() {
    this.status = PostStatus.ARCHIVE;
  }


  /**
   * 새로운 댓글 생성
   *
   * @param userId
   * @param comment
   */
  public PostComment addComment(String userId, String comment) {
    return
        PostComment.newComment(this.postId, userId, comment);
  }

  /*Test*/
  @VisibleForTesting
  public void setPostId(String postId) {
    this.postId = postId;
  }

  public static Post newTestPost(String postId, String userId, String content,
      int viewCount, PostStatus status
  ) {
    return new Post(
        postId,
        userId,
        content,
        viewCount,
        status,
        null
    );
  }
}
