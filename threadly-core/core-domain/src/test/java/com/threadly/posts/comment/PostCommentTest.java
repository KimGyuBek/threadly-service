package com.threadly.posts.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.threadly.posts.Post;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 게시글 댓글 도메인 테스트
 */
class PostCommentTest {

  private Post post;

  /**
   * like()
   */
  /*[Case #1] 사용자가 처음 좋아요 눌렀을때 증가해야한다*/
  @DisplayName("사용자가 처음 좋아요 눌렀을때 증가해야한다")
  @Test
  public void like_shouldIncreaseLikeCount_whenLikesComment() throws Exception {
    //given
    /*게시글 생성*/
    generatePost();

    String userId = "user1";
    /*댓글 생성*/
    PostComment postComment = post.addComment(userId, "comment1");
    postComment.setCommentId("comment1");

    //when
    /*댓글 좋아요*/
    postComment.like(userId);

    //then
    assertThat(postComment.getLikesCount()).isEqualTo(1);
  }

//  /*[Case #2] 중복 좋아요 방지*/
//  @DisplayName("중복 좋아요 방지")
//  @Test
//  public void like_shouldNotDuplicateLikes_whenSameUserLikesMultipleTimes() throws Exception {
//    //given
//    /*게시글 생성*/
//    generatePost();
//
//    String userId = "user1";
//    /*댓글 생성*/
//    PostComment postComment = post.addComment(userId, "comment1");
//    postComment.setCommentId("comment1");
//
//    //when
//    /*댓글 좋아요*/
//    for (int i = 0; i < 50; i++) {
//      postComment.like(userId);
//    }
//
//    //then
//    assertEquals(postComment.getLikesCount(), 1);
//  }

  /**
   * unlike()
   */
  /*[Case #1] 사용자의 좋아요 기록이 없을 경우 좋아요 취소해도 무효*/
  @DisplayName("unlike - 사용자의 좋아요 기록이 없을 경우 좋아요 취소해도 무효")
  @Test
  public void unlike_shouldDoNothing_whenUserUnlikeWithoutLikeHistory() throws Exception {
    //given
    generatePost();

    String userId = "user1";
    String commentId = "comment1";
    /*댓글 생성*/
    PostComment postComment = post.addComment(userId, "comment1");
    postComment.setCommentId(commentId);

    //when
    postComment.unlike(userId);

    //then
    assertEquals(postComment.getLikesCount(), 0);
  }

  /*[Case #2] 사용자가 좋아요한 댓글에 좋아요 취소를 했을 경우 좋아요 수 감소*/
  @DisplayName("사용자가 좋아요한 댓글에 좋아요 취소를 했을 경우 좋아요 수 감소")
  @Test
  public void unlike_shouldRemoveLike_whenUserPreviouslyLiked() throws Exception {
    //given
    generatePost();

    String userId = "user1";
    String commentId = "comment1";
    /*댓글 생성*/
    PostComment postComment = post.addComment(userId, "comment1");
    postComment.setCommentId(commentId);

    /*댓글 좋아요*/
    postComment.like(userId);

    //when
    postComment.unlike(userId);

    //then
    assertEquals(postComment.getLikesCount(), 0);
  }

  /*[Case #3] 여러명 중 특정 사용자 취소 시 그 사람의 좋아요만 취소되어야 함*/
  @DisplayName("여러명 중 특정 사용자 취소 시 그 사람의 좋아요만 취소되어야 함")
  @Test
  public void unlike_shouldRemoveOnlyOneLike_whenUserAmongMultipleLiked() throws Exception {
    //given
    int SIZE = 100;

    generatePost();

    String commentId = "comment";

    /*댓글 생성*/
    PostComment postComment = post.addComment("user", "comment1");
    postComment.setCommentId(commentId);

    /*댓글 좋아요*/
    List<String> userIds = new ArrayList<>();
    for (int i = 0; i < SIZE; i++) {
      userIds.add("user" + (i + 1));
      postComment.like(userIds.get(i));
    }

    //when
    postComment.unlike(userIds.get(0));

    //then
    assertThat(postComment.getLikesCount()).isEqualTo(SIZE - 1);
    assertThat(postComment.getLikesList().size()).isEqualTo(SIZE - 1);
  }

  /**
   * getLikesList()
   */
  /*[Case #1] 전체 좋아요 목록을 반환해야 한다*/
  @DisplayName("getLikesList - 전체 좋아요 목록을 반환해야 한다")
  @Test
  public void getLikesList() throws Exception {
    int MAX = 100;
    //given

    /*게시물 생성*/
    generatePost();

    /*댓글 작성*/
    PostComment newComment = post.addComment("user1", "comment1");
    newComment.setCommentId("com1");


    /*여러번 좋아요*/
    List<CommentLike> likes = new ArrayList<>();
    for (int i = 0; i < MAX; i++) {
      likes.add(newComment.like("user" + i));
    }

    //when
    List<CommentLike> likesList = newComment.getLikesList();

    //then
    assertThat(likesList.size()).isEqualTo(MAX);
    assertThat(likesList).containsAll(likes);
  }

  /*게시글 생성*/
  private void generatePost() {
    String userId = "user1";
    String content = "content";
    post = Post.newPost(userId, content);
    post.setPostId("pos1");
  }
}