package com.threadly.posts.comment;

import static org.assertj.core.api.Assertions.assertThat;

import com.threadly.core.domain.post.Post;

/**
 * 게시글 댓글 도메인 테스트
 */
class PostCommentTest {

  private Post post;

  /**
   * like()
   */

  /*게시글 생성*/
  private void generatePost() {
    String userId = "user1";
    String content = "content";
    post = Post.newPost(userId, content);
    post.setPostId("pos1");
  }

}