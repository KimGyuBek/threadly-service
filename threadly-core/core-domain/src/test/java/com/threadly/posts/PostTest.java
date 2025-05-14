package com.threadly.posts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Post 도메인 테스트
 */
class PostTest {

  private Post post;

  /**
   * newPost()
   */
  /*[Case #1] 게시글 생성시 postId, content가 올바르게 세팅되는지 확인*/
  @DisplayName("newPost - 게시글이 정상적으로 생성되어야 한다")
  @Test
  public void newPost_shouldCreatePostSuccessfully() throws Exception {
    //given
    String userId = "user1";
    String content = "content";

    //when
    /*게시물 생성*/
    post = Post.newPost(userId, content);

    //then
    assertAll(
        () -> assertThat(post.getUserId()).isEqualTo(userId),
        () -> assertThat(post.getContent()).isEqualTo(content)
    );

  }

  /**
   * increaseViewCount()
   */
  /*[Case #1] 호출 시 1 증가해야 한다*/
  @Test
  @DisplayName("increaseViewCount - increaseViewCount 호출 시 조회수가 1 증가해야 한다")
  public void increaseViewCount_shouldIncreaseViewCountByOne() throws Exception {
    //given
    /*게시글 생성*/
    generatePost();

    //when
    post.increaseViewCount();

    //then
    assertThat(post.getViewCount()).isEqualTo(1);
  }

  /*[Case #2] 여러 번 호출 시 누적 증가 확인*/
  @Test
  @DisplayName("increaseViewCount - increaseViewCount  반복 호출 시 조회수가 누적 증가해야 한다")
  public void increaseViewCount_shouldIncreaseViewCountByMaxTimes() throws Exception {
    int MAX = 100;

    //given
    /*게시글 생성*/
    generatePost();

    //when
    for (int i = 0; i < MAX; i++) {
      post.increaseViewCount();
    }

    //then
    assertThat(post.getViewCount()).isEqualTo(MAX);
  }

  /**
   * like()
   */
  /*[Case #1] 10명의 사용자가 좋아요 눌렀을때 누적 확인*/
  @Test
  @DisplayName("like - 여러 사용자가 좋아요 추가 시 개수 누적되어야 한다")
  public void like_shouldAddLikesMultipleUsers() throws Exception {
    //given
    String[] userIds = {"user1", "user2", "user3", "user4", "user5", "user6", "user7", "user8",
        "user9", "user10"};
    /*게시글 생성*/
    generatePost();

    //when
    for (String userId : userIds) {
      post.like(userId);
    }

    //then
    assertThat(post.getLikesCount()).isEqualTo(10);
  }

  /*[Case #2] 동일 사용자가 게시글에 여러 번 좋아요를 눌러도 중복 추가되지 않아야 한다*/
  @Test
  @DisplayName("like - 동일한 사용자의 중복 좋아요는 1회만 인정되어야 한다")
  public void like_shouldNotDuplicateLikeFromSameUser() throws Exception {
    //given
    String userId = "user1";

    /*게시글 생성*/
    generatePost();

    //when
    for (int i = 0; i < 5; i++) {
      post.like(userId);
    }

    //then
    assertThat(post.getLikesCount()).isEqualTo(1);
  }

  /**
   * unlike()
   */
  /*[Case #1] 좋아요 후 취소 시 좋아요 수가 감소하는지 확인 */
  @DisplayName("unlike - 좋아요 취소 시 해당 사용자의 좋아요가 삭제 되어야 한다")
  @Test
  public void unlike_shouldRemoveLike_whenUserUnlikes() throws Exception {
    //given

    /*게시글 생성*/
    generatePost();

    /*좋아요*/
    String userId = "user1";
    post.like(userId);
    int likesCount1 = post.getLikesCount();

    //when
    /*싫어요*/
    post.unlike(userId);
    int likesCount2 = post.getLikesCount();

    //then
    assertAll(
        () -> assertThat(likesCount1).isEqualTo(1),
        () -> assertThat(likesCount2).isEqualTo(0)
    );
  }

  /*[Case #2] 동일 사용자가 게시글에 좋아요를 누른 후 여러번 좋아요 취소를 눌러도 오류 없이 동작해야 한다.*/
  @DisplayName("unlike - 중복해서 좋아요 취소해도 오류가 없어야 한다")
  @Test
  public void unlike_shouldNotFail_whenUnlikingMultipleTimes()
      throws Exception {
    //given

    /*게시글 생성*/
    generatePost();

    /*좋아요*/
    String userId = "user1";
    post.like(userId);

    //when
    /*싫어요*/
    for (int i = 0; i < 5; i++) {
      post.unlike(userId);
    }
    int likesCount = post.getLikesCount();

    //then
    assertAll(
        () -> assertThat(likesCount).isEqualTo(0)
    );
  }

  /*[Case #3] 좋아요 안 한 사용자가 취소해도 영향 없어야 함*/
  @DisplayName("unlike -  좋아요 기록이 없는 사용자가 취소할 경우 무시되어야 한다")
  @Test
  public void unlike_shouldDoNothing_whenUserNeverLiked() throws Exception {
    //given

    /*게시글 생성*/
    generatePost();

    String userId = "user1";

    //when
    /*싫어요*/
    for (int i = 0; i < 5; i++) {
      post.unlike(userId);
    }
    int likesCount = post.getLikesCount();

    //then
    assertAll(
        () -> assertThat(likesCount).isEqualTo(0)
    );
  }

  /**
   * newComment()
   */
  /*[Case #1] 새로운 댓글 추가*/
  @DisplayName("addComment - 댓글이 정상적으로 추가되어야 한다")
  @Test
  public void addComment_shouldAddCommentSuccessfully() throws Exception {
    //given
    /*게시글 생성*/
    generatePost();

    String userId = "user1";
    String comment = "comment";
    //when
    PostComment newComment = post.addComment(userId, "comment");

    //then
    assertThat(post.getCommentsCount()).isEqualTo(1);
    assertAll(
        () -> assertThat(newComment.getUserId()).isEqualTo(userId),
        () -> assertThat(newComment.getContent()).isEqualTo(comment),
        () -> assertThat(newComment.getPostId()).isEqualTo(post.getPostId())
    );
  }

  /*[Case #2] 댓글 반복 생성시 올바르게 저장되는지 확인*/
  @DisplayName("addComment - 반복 호출 시 여러 댓글이 정상 추가 되어야 한다")
  @Test
  public void addComment_shouldAddMultipleComments() throws Exception {
    //given
    generatePost();

    //when
    String userId = "user1";
    List<PostComment> comments = new ArrayList<>();
    for (int i = 0; i < 50; i++) {
      comments.add(post.addComment(userId, "comment" + (i + 1)));
    }

    //then
    assertThat(post.getCommentsCount()).isEqualTo(50);

    for (int i = 0; i < comments.size(); i++) {
      assertThat(comments.get(i).getUserId()).isEqualTo(userId);
      assertThat(comments.get(i).getPostId()).isEqualTo(post.getPostId());
      assertThat(comments.get(i).getContent()).isEqualTo("comment" + (i + 1));
    }
  }

  /**
   * removeComment()
   */
  /*[Case #1] 특정 댓글을 삭제 성공 후 댓글 수 확인*/
  @DisplayName("removeComment - 특정 댓글을 삭제하면 true를 반환하고 list에서 지워져야 한다")
  @Test
  public void removeComment_shouldRemoveCommentById() throws Exception {
    //given
    generatePost();
    String userId = "user1";
    String comment = "comment";
    String commentId = "com1";

    /*댓글 생성*/
    PostComment newComment = post.addComment(userId, comment);
    newComment.setCommentId(commentId);

    //when
    boolean result = post.removeComment(commentId);

    //then
    assertTrue(result);
    assertThat(post.getCommentsCount()).isEqualTo(0);
  }

  /*[Case #2] 존재하는 하지 않는 댓글 삭제 시 false를 반환해야 함 */
  @DisplayName("removeComment - 존재하지 않는 댓글 삭제 시 false를 반환햐야 한다")
  @Test
  public void removeComment_shouldReturnFalse_whenCommentNotFound() throws Exception {
    //given
    generatePost();
    String commentId = "com1";

    //when
    boolean result = post.removeComment(commentId);

    //then
    assertFalse(result);
    assertThat(post.getCommentsCount()).isEqualTo(0);
  }

  /*[Case #3] 여러개의 댓글 중 특정 댓글만 삭제해야함*/
  @DisplayName("removeComment - 여러 댓글 중 특정 댓글만 삭제되어야 한다")
  @Test
  public void removeComment_shouldRemoveOnlyTargetedCommentAmongMany() throws Exception {
    int MAX = 100;
    //given
    /*게시글 생성*/
    generatePost();

    //when
    /*댓글 추가*/
    String userId = "user1";
    List<PostComment> comments = new ArrayList<>();
    for (int i = 0; i < MAX; i++) {
      PostComment newComment = post.addComment(userId, "comment" + (i + 1));
      newComment.setCommentId("com" + (i + 1));
      comments.add(newComment);
    }

    //then
    assertTrue(post.removeComment("com1"));
    assertThat(post.getCommentsCount()).isEqualTo(MAX - 1);
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

    /*여러번 좋아요*/
    List<PostLike> likes = new ArrayList<>();
    for (int i = 0; i < MAX; i++) {
      likes.add(post.like("user" + i));
    }

    //when
    List<PostLike> postLikeList = post.getLikesList();

    //then
    assertThat(postLikeList.size()).isEqualTo(MAX);
    assertThat(postLikeList).containsAll(likes);
  }

  /**
   * updateContent()
   */
  /*[Case #1] 게시글 content 수정 시 변경된 내용이 반환되어야 한다 */
  @DisplayName("updateContent - 게시글 content 수정 시 변경된 내용이 반환되어야 한다")
  @Test
  public void updateContent_UpdatePostContet() throws Exception {
    //given
    generatePost();

    String content = post.getContent();

    //when
    String newContent = "newContent";

    /*content 업데이트*/
    post.updateContent(newContent);

    //then
    assertFalse(content.equals(newContent));

  }

  /**
   * 게시글 생성
   */
  private void generatePost() {
    String userId = "user1";
    String content = "content";
    post = Post.newPost(userId, content);
    post.setPostId("pos1");
  }

}