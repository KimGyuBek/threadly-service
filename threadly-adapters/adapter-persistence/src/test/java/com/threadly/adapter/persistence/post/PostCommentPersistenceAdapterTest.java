package com.threadly.adapter.persistence.post;

import static org.assertj.core.api.Assertions.assertThat;

import com.threadly.BasePersistenceTest;
import com.threadly.adapter.persistence.post.adapter.PostCommentPersistenceAdapter;
import com.threadly.adapter.persistence.post.adapter.PostPersistenceAdapter;
import com.threadly.core.domain.post.Post;
import com.threadly.core.domain.post.PostCommentStatus;
import com.threadly.core.domain.post.PostStatus;
import com.threadly.core.domain.post.comment.PostComment;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.ClassOrderer;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * PostCommentPersistenceAdapter 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class PostCommentPersistenceAdapterTest extends BasePersistenceTest {

  @Autowired
  private PostCommentPersistenceAdapter postCommentPersistenceAdapter;

  @Autowired
  private PostPersistenceAdapter postPersistenceAdapter;

  private static final String TEST_POST_ID = "test-comment-post-id-1";
  private static final String TEST_COMMENT_ID = "test-comment-id-1";
  private static final String TEST_COMMENT_CONTENT = "테스트 댓글 내용입니다";

  /**
   * 테스트용 게시글 생성
   */
  private Post createTestPost() {
    createTestUser();
    Post post = Post.newTestPost(
        TEST_POST_ID,
        TEST_USER_ID,
        "댓글 테스트 게시글",
        0,
        PostStatus.ACTIVE
    );
    return postPersistenceAdapter.savePost(post);
  }

  /**
   * 테스트용 댓글 생성
   */
  private PostComment createTestComment() {
    createTestPost();
    PostComment comment = PostComment.newTestComment(
        TEST_COMMENT_ID,
        TEST_POST_ID,
        TEST_USER_ID,
        TEST_COMMENT_CONTENT,
        PostCommentStatus.ACTIVE
    );
    postCommentPersistenceAdapter.savePostComment(comment);
    entityManager.flush();
    entityManager.clear();
    return comment;
  }

  @Order(1)
  @DisplayName("댓글 저장 테스트")
  @Nested
  class SavePostCommentTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 댓글이 정상적으로 저장된다")
      @Test
      void savePostComment_shouldSaveComment_whenValid() {
        //given
        createTestPost();
        String commentId = "new-comment-id";
        String content = "새로운 댓글";
        PostComment comment = PostComment.newTestComment(
            commentId,
            TEST_POST_ID,
            TEST_USER_ID,
            content,
            PostCommentStatus.ACTIVE
        );

        //when
        postCommentPersistenceAdapter.savePostComment(comment);
        entityManager.flush();
        entityManager.clear();

        //then
        Optional<PostComment> result = postCommentPersistenceAdapter.fetchById(commentId);
        assertThat(result).isPresent();
        assertThat(result.get().getCommentId()).isEqualTo(commentId);
        assertThat(result.get().getContent()).isEqualTo(content);
      }
    }
  }

  @Order(2)
  @DisplayName("댓글 ID로 조회 테스트")
  @Nested
  class FetchByIdTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 존재하는 commentId로 조회 시 댓글이 반환된다")
      @Test
      void fetchById_shouldReturnComment_whenCommentExists() {
        //given
        createTestComment();
        String commentId = TEST_COMMENT_ID;

        //when
        Optional<PostComment> result = postCommentPersistenceAdapter.fetchById(commentId);

        //then
        assertThat(result).isPresent();
        assertThat(result.get().getCommentId()).isEqualTo(commentId);
        assertThat(result.get().getContent()).isEqualTo(TEST_COMMENT_CONTENT);
        assertThat(result.get().getPostId()).isEqualTo(TEST_POST_ID);
        assertThat(result.get().getUserId()).isEqualTo(TEST_USER_ID);
      }
    }

    @DisplayName("실패")
    @Nested
    class Fail {

      @DisplayName("1. 존재하지 않는 commentId로 조회 시 빈 Optional이 반환된다")
      @Test
      void fetchById_shouldReturnEmpty_whenCommentDoesNotExist() {
        //given
        String nonExistentCommentId = "non-existent-comment-id";

        //when
        Optional<PostComment> result = postCommentPersistenceAdapter.fetchById(
            nonExistentCommentId);

        //then
        assertThat(result).isEmpty();
      }
    }
  }

  @Order(3)
  @DisplayName("댓글 상태 변경 테스트")
  @Nested
  class UpdatePostCommentStatusTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 댓글 상태가 DELETED로 정상적으로 변경된다")
      @Test
      void updatePostCommentStatus_shouldUpdateStatus_whenCommentExists() {
        //given
        createTestComment();
        String commentId = TEST_COMMENT_ID;
        PostCommentStatus newStatus = PostCommentStatus.DELETED;

        //when
        postCommentPersistenceAdapter.updatePostCommentStatus(commentId, newStatus);
        entityManager.flush();
        entityManager.clear();

        //then
        Optional<PostComment> result = postCommentPersistenceAdapter.fetchById(commentId);
        assertThat(result).isPresent();
        assertThat(result.get().getStatus()).isEqualTo(newStatus);
      }
    }
  }

  @Order(4)
  @DisplayName("댓글 상태 조회 테스트")
  @Nested
  class FetchCommentStatusTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 댓글 상태가 정상적으로 조회된다")
      @Test
      void fetchCommentStatus_shouldReturnStatus_whenCommentExists() {
        //given
        createTestComment();
        String commentId = TEST_COMMENT_ID;

        //when
        Optional<PostCommentStatus> result = postCommentPersistenceAdapter.fetchCommentStatus(
            commentId);

        //then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(PostCommentStatus.ACTIVE);
      }
    }

    @DisplayName("실패")
    @Nested
    class Fail {

      @DisplayName("1. 존재하지 않는 댓글의 경우 빈 Optional이 반환된다")
      @Test
      void fetchCommentStatus_shouldReturnEmpty_whenCommentDoesNotExist() {
        //given
        String nonExistentCommentId = "non-existent-comment-id";

        //when
        Optional<PostCommentStatus> result = postCommentPersistenceAdapter.fetchCommentStatus(
            nonExistentCommentId);

        //then
        assertThat(result).isEmpty();
      }
    }
  }

  @Order(5)
  @DisplayName("게시글의 모든 댓글 상태 변경 테스트")
  @Nested
  class UpdateAllCommentStatusByPostIdTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 게시글의 모든 댓글 상태가 변경된다")
      @Test
      void updateAllCommentStatusByPostId_shouldUpdateAll_whenCommentsExist() {
        //given
        createTestComment();
        PostComment comment2 = PostComment.newTestComment(
            "test-comment-id-2",
            TEST_POST_ID,
            TEST_USER_ID,
            "두번째 댓글",
            PostCommentStatus.ACTIVE
        );
        postCommentPersistenceAdapter.savePostComment(comment2);
        entityManager.flush();
        entityManager.clear();

        PostCommentStatus newStatus = PostCommentStatus.DELETED;

        //when
        postCommentPersistenceAdapter.updateAllCommentStatusByPostId(TEST_POST_ID, newStatus);
        entityManager.flush();
        entityManager.clear();

        //then
        Optional<PostComment> result1 = postCommentPersistenceAdapter.fetchById(TEST_COMMENT_ID);
        Optional<PostComment> result2 = postCommentPersistenceAdapter.fetchById(
            "test-comment-id-2");
        assertThat(result1).isPresent();
        assertThat(result1.get().getStatus()).isEqualTo(newStatus);
        assertThat(result2).isPresent();
        assertThat(result2.get().getStatus()).isEqualTo(newStatus);
      }
    }
  }
}
