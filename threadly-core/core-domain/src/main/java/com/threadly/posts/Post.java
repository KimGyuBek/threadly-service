package com.threadly.posts;

import com.google.common.annotations.VisibleForTesting;
import com.threadly.posts.comment.PostComment;
import com.threadly.util.RandomUtils;
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

  private PostStatusType status;

  private LocalDateTime postedAt;

  public Post(String postId, String userId, String content, int viewCount, PostStatusType status,
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
        PostStatusType.ACTIVE,
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
    if (status != PostStatusType.ACTIVE) {
      throw new CannotLikePostException();
    }
  }

  /**
   * 좋아요 생성
   *
   * @param userId
   * @return
   */
  public PostLike like(String userId) {
    return
        PostLike.newLike(this.postId, userId);
  }


  /**
   * 게시글 삭제 상태로 변경
   */
  public void markAsDeleted() {
    this.status = PostStatusType.DELETED;
  }

  /**
   * 게시글 블라인드 상태로 변경
   */
  public void markAsBlocked() {
    this.status = PostStatusType.BLOCKED;
  }

  /**
   * 게시글 블라인드 해제 상태로 변경
   */
  public void markAsUnblocked() {
    this.status = PostStatusType.ACTIVE;
  }

  /**
   * 게시글 아카이브 상태로 변경
   */
  public void markAsArchived() {
    this.status = PostStatusType.ARCHIVE;
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
      int viewCount, PostStatusType status
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
