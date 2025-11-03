package com.threadly.adapter.persistence.post;

import static org.assertj.core.api.Assertions.assertThat;

import com.threadly.BasePersistenceTest;
import com.threadly.adapter.persistence.post.adapter.PostLikePersistenceAdapter;
import com.threadly.adapter.persistence.post.adapter.PostPersistenceAdapter;
import com.threadly.core.domain.post.Post;
import com.threadly.core.domain.post.PostLike;
import com.threadly.core.domain.post.PostStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.ClassOrderer;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * PostLikePersistenceAdapter 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class PostLikePersistenceAdapterTest extends BasePersistenceTest {

  @Autowired
  private PostLikePersistenceAdapter postLikePersistenceAdapter;

  @Autowired
  private PostPersistenceAdapter postPersistenceAdapter;

  private static final String TEST_POST_ID = "test-post-like-id-1";
  private static final String TEST_USER_ID_2 = "test-user-id-2";

  /**
   * 테스트용 게시글 생성
   */
  private Post createTestPost() {
    createTestUser();
    Post post = Post.newTestPost(
        TEST_POST_ID,
        TEST_USER_ID,
        "좋아요 테스트 게시글",
        0,
        PostStatus.ACTIVE
    );
    return postPersistenceAdapter.savePost(post);
  }

  /**
   * 테스트용 좋아요 생성
   */
  private PostLike createTestPostLike(String postId, String userId) {
    PostLike postLike = PostLike.newLike(postId, userId);
    postLikePersistenceAdapter.createPostLike(postLike);
    entityManager.flush();
    entityManager.clear();
    return postLike;
  }

  @Order(1)
  @DisplayName("게시글 좋아요 생성 테스트")
  @Nested
  class CreatePostLikeTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 게시글 좋아요가 정상적으로 생성된다")
      @Test
      void createPostLike_shouldCreateLike_whenValid() {
        //given
        createTestPost();
        String postId = TEST_POST_ID;
        String userId = TEST_USER_ID;

        //when
        PostLike postLike = PostLike.newLike(postId, userId);
        postLikePersistenceAdapter.createPostLike(postLike);
        entityManager.flush();
        entityManager.clear();

        //then
        boolean exists = postLikePersistenceAdapter.existsByPostIdAndUserId(postId, userId);
        assertThat(exists).isTrue();
      }
    }
  }

  @Order(2)
  @DisplayName("게시글 좋아요 존재 여부 확인 테스트")
  @Nested
  class ExistsByPostIdAndUserIdTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 존재하는 좋아요의 경우 true를 반환한다")
      @Test
      void existsByPostIdAndUserId_shouldReturnTrue_whenLikeExists() {
        //given
        createTestPost();
        createTestPostLike(TEST_POST_ID, TEST_USER_ID);

        //when
        boolean exists = postLikePersistenceAdapter.existsByPostIdAndUserId(TEST_POST_ID,
            TEST_USER_ID);

        //then
        assertThat(exists).isTrue();
      }

      @DisplayName("2. 존재하지 않는 좋아요의 경우 false를 반환한다")
      @Test
      void existsByPostIdAndUserId_shouldReturnFalse_whenLikeDoesNotExist() {
        //given
        createTestPost();
        String nonExistentUserId = "non-existent-user";

        //when
        boolean exists = postLikePersistenceAdapter.existsByPostIdAndUserId(TEST_POST_ID,
            nonExistentUserId);

        //then
        assertThat(exists).isFalse();
      }
    }
  }

  @Order(3)
  @DisplayName("게시글별 좋아요 개수 조회 테스트")
  @Nested
  class FetchLikeCountByPostIdTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 게시글의 좋아요 개수가 정상적으로 반환된다")
      @Test
      void fetchLikeCountByPostId_shouldReturnCount_whenLikesExist() {
        //given
        createTestPost();
        createTestPostLike(TEST_POST_ID, TEST_USER_ID);
        createUser(TEST_USER_ID_2, "test2@example.com", "테스트유저2");
        createTestPostLike(TEST_POST_ID, TEST_USER_ID_2);

        //when
        long likeCount = postLikePersistenceAdapter.fetchLikeCountByPostId(TEST_POST_ID);

        //then
        assertThat(likeCount).isEqualTo(2);
      }

      @DisplayName("2. 좋아요가 없는 게시글의 경우 0을 반환한다")
      @Test
      void fetchLikeCountByPostId_shouldReturnZero_whenNoLikes() {
        //given
        createTestPost();

        //when
        long likeCount = postLikePersistenceAdapter.fetchLikeCountByPostId(TEST_POST_ID);

        //then
        assertThat(likeCount).isEqualTo(0);
      }
    }
  }

  @Order(4)
  @DisplayName("게시글 좋아요 삭제 테스트")
  @Nested
  class DeleteByPostIdAndUserIdTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 좋아요가 정상적으로 삭제된다")
      @Test
      void deleteByPostIdAndUserId_shouldDelete_whenLikeExists() {
        //given
        createTestPost();
        createTestPostLike(TEST_POST_ID, TEST_USER_ID);

        //when
        int deletedCount = postLikePersistenceAdapter.deleteByPostIdAndUserId(TEST_POST_ID,
            TEST_USER_ID);
        entityManager.flush();
        entityManager.clear();

        //then
        assertThat(deletedCount).isEqualTo(1);
        boolean exists = postLikePersistenceAdapter.existsByPostIdAndUserId(TEST_POST_ID,
            TEST_USER_ID);
        assertThat(exists).isFalse();
      }

      @DisplayName("2. 존재하지 않는 좋아요 삭제 시 0을 반환한다")
      @Test
      void deleteByPostIdAndUserId_shouldReturnZero_whenLikeDoesNotExist() {
        //given
        createTestPost();
        String nonExistentUserId = "non-existent-user";

        //when
        int deletedCount = postLikePersistenceAdapter.deleteByPostIdAndUserId(TEST_POST_ID,
            nonExistentUserId);

        //then
        assertThat(deletedCount).isEqualTo(0);
      }
    }
  }

  @Order(5)
  @DisplayName("게시글의 모든 좋아요 삭제 테스트")
  @Nested
  class DeleteAllByPostIdTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 게시글의 모든 좋아요가 삭제된다")
      @Test
      void deleteAllByPostId_shouldDeleteAll_whenLikesExist() {
        //given
        createTestPost();
        createTestPostLike(TEST_POST_ID, TEST_USER_ID);
        createUser(TEST_USER_ID_2, "test2@example.com", "테스트유저2");
        createTestPostLike(TEST_POST_ID, TEST_USER_ID_2);

        //when
        postLikePersistenceAdapter.deleteAllByPostId(TEST_POST_ID);
        entityManager.flush();
        entityManager.clear();

        //then
        long likeCount = postLikePersistenceAdapter.fetchLikeCountByPostId(TEST_POST_ID);
        assertThat(likeCount).isEqualTo(0);
      }
    }
  }
}
