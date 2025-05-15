package com.threadly.posts;

import com.google.common.annotations.VisibleForTesting;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

/**
 * Post 도메인
 */
@Getter
@Builder
public class Post {

  private String postId;
  private String userId;
  private String content;

  private Set<PostLike> postLikes;
  private List<PostComment> postComments;

  private int viewCount;
  private LocalDateTime postedAt;

  public Post(String postId, String content, String userId, int viewCount, LocalDateTime postedAt) {
    this.postId = postId;
    this.content = content;
    this.userId = userId;
    this.viewCount = viewCount;
    this.postedAt = postedAt;
  }

  private Post(String postId, String userId, String content, Set<PostLike> postLikes,
      List<PostComment> postComments, int viewCount, LocalDateTime postedAt) {
    this.postId = postId;
    this.userId = userId;
    this.content = content;
    this.postLikes = postLikes != null ? postLikes : new HashSet<>();
    this.postComments = postComments != null ? postComments : new ArrayList<>();
    this.viewCount = viewCount;
    this.postedAt = postedAt;
  }

  /**
   * 새로운 게시글 생성
   *
   * @param userId
   * @param content
   * @return
   */
  public static Post newPost(String userId, String content) {
    return Post.builder()
        .postId(null)
        .userId(userId)
        .content((content != null) ? content : "")
        .postLikes(new HashSet<>())
        .postComments(new ArrayList<>())
        .viewCount(0)
        .postedAt(LocalDateTime.now())
        .build();
  }

  /**
   * content 수정
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
   * 좋아요 수 조회
   *
   * @return
   */
  public int getLikesCount() {
    return postLikes != null ? postLikes.size() : 0;
  }

  /**
   * 좋아요 생성
   *
   * @return
   * @param userId
   */
  public PostLike like(String userId) {
    PostLike newLike = new PostLike(this.postId, userId);
    postLikes.add(newLike);
    return newLike;
  }

  /**
   * 좋아요 취소
   *
   * @param userId
   */
  public void unlike(String userId) {
    postLikes.removeIf(postLike -> postLike.getUserId().equals(userId));
  }

  /**
   * 좋아요 목록 조회
   */
  public List<PostLike> getLikesList() {
    return new ArrayList<>(postLikes);
  }

  /**
   * 댓글 수 조회
   *
   * @return
   */
  public int getCommentsCount() {
    return postComments != null ? postComments.size() : 0;
  }

  /**
   * 새로운 댓글 생성
   *
   * @param userId
   * @param comment
   */
  public PostComment addComment(String userId, String comment) {
    PostComment newComment = PostComment.newComment(this.postId, userId, comment);
    postComments.add(newComment);

    return newComment;
  }

  /**
   * 댓글 삭제
   *
   * @param commentId
   * @return
   */
  public boolean removeComment(String commentId) {
    return
        postComments.removeIf(postComment -> postComment.getCommentId().equals(commentId));
  }

  /**
   * commentId로 comment 조회
   *
   * @param commentId
   * @return
   */
  private PostComment findCommentById(String commentId) {
    PostComment postComment = postComments.stream().filter(
        comment -> comment.getCommentId().equals(commentId)
    ).findFirst().orElse(null);
    return postComment;
  }

  /*Test*/
  @VisibleForTesting
  void setPostId(String postId) {
    this.postId = postId;
  }

}
