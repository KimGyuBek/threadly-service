package com.threadly.posts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.threadly.posts.comment.PostComment;
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