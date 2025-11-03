package com.threadly.posts.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.threadly.core.domain.post.PostCommentStatus;
import com.threadly.core.domain.post.PostStatus;
import com.threadly.core.domain.post.comment.CannotDeleteCommentException.AlreadyDeletedException;
import com.threadly.core.domain.post.comment.CannotDeleteCommentException.WriteMismatchException;
import com.threadly.core.domain.post.comment.CannotLikePostCommentException;
import com.threadly.core.domain.post.comment.CommentLike;
import com.threadly.core.domain.post.comment.PostComment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 게시글 댓글 도메인 테스트
 */
class PostCommentTest {

  private PostComment comment;

  /**
   * newComment()
   */
  /*[Case #1] 댓글 생성시 필드가 올바르게 세팅되는지 확인*/
  @DisplayName("newComment - 댓글이 정상적으로 생성되어야 한다")
  @Test
  public void newComment_shouldCreateCommentSuccessfully() throws Exception {
    //given
    String postId = "post1";
    String userId = "user1";
    String content = "comment content";

    //when
    comment = PostComment.newComment(postId, userId, content);

    //then
    assertAll(
        () -> assertThat(comment.getPostId()).isEqualTo(postId),
        () -> assertThat(comment.getUserId()).isEqualTo(userId),
        () -> assertThat(comment.getContent()).isEqualTo(content),
        () -> assertThat(comment.getStatus()).isEqualTo(PostCommentStatus.ACTIVE)
    );
  }

  /**
   * markAsDeleted()
   */
  /*[Case #1] ACTIVE 상태에서 DELETED로 변경되어야 한다*/
  @DisplayName("markAsDeleted - DELETED 상태로 변경되어야 한다")
  @Test
  public void markAsDeleted_shouldChangeStatusToDeleted() throws Exception {
    //given
    generateComment();

    //when
    comment.markAsDeleted();

    //then
    assertThat(comment.getStatus()).isEqualTo(PostCommentStatus.DELETED);
  }

  /**
   * validateDeletableBy()
   */
  /*[Case #1] 작성자가 아닌 경우 예외가 발생해야 한다*/
  @DisplayName("validateDeletableBy - 작성자가 아닌 경우 예외 발생")
  @Test
  public void validateDeletableBy_shouldThrowException_whenUserNotWriter() throws Exception {
    //given
    generateComment();

    //when & then
    assertThrows(WriteMismatchException.class,
        () -> comment.validateDeletableBy("user2", PostStatus.ACTIVE));
  }

  /*[Case #2] DELETED 상태인 경우 예외가 발생해야 한다*/
  @DisplayName("validateDeletableBy - DELETED 상태인 경우 예외 발생")
  @Test
  public void validateDeletableBy_shouldThrowException_whenAlreadyDeleted() throws Exception {
    //given
    generateComment();
    comment.markAsDeleted();

    //when & then
    assertThrows(AlreadyDeletedException.class,
        () -> comment.validateDeletableBy("user1", PostStatus.ACTIVE));
  }

  /**
   * validateLikeable()
   */
  /*[Case #1] ACTIVE가 아닌 상태에서 예외가 발생해야 한다*/
  @DisplayName("validateLikeable - DELETED 상태에서 예외 발생")
  @Test
  public void validateLikeable_shouldThrowException_whenNotActive() throws Exception {
    //given
    generateComment();
    comment.markAsDeleted();

    //when & then
    assertThrows(CannotLikePostCommentException.class, () -> comment.validateLikeable());
  }

  /**
   * like()
   */
  /*[Case #1] 좋아요가 정상적으로 생성되어야 한다*/
  @DisplayName("like - 댓글 좋아요가 정상적으로 생성되어야 한다")
  @Test
  public void like_shouldCreateLikeSuccessfully() throws Exception {
    //given
    generateComment();

    String userId = "user2";

    //when
    CommentLike like = comment.like(userId);

    //then
    assertAll(
        () -> assertThat(like.getCommentId()).isEqualTo(comment.getCommentId()),
        () -> assertThat(like.getUserId()).isEqualTo(userId)
    );
  }

  /*댓글 생성*/
  private void generateComment() {
    comment = PostComment.newComment("post1", "user1", "comment");
  }
}