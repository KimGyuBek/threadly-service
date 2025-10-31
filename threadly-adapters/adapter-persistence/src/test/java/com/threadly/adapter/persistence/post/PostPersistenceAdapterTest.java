package com.threadly.adapter.persistence.post;

import static org.assertj.core.api.Assertions.assertThat;

import com.threadly.BasePersistenceTest;
import com.threadly.adapter.persistence.post.adapter.PostPersistenceAdapter;
import com.threadly.core.domain.post.Post;
import com.threadly.core.domain.post.PostStatus;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.ClassOrderer;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * PostPersistenceAdapter 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class PostPersistenceAdapterTest extends BasePersistenceTest {

  @Autowired
  private PostPersistenceAdapter postPersistenceAdapter;

  private static final String TEST_POST_ID = "test-post-id-1";
  private static final String TEST_POST_CONTENT = "테스트 게시글 내용입니다";

  /**
   * 테스트용 게시글 생성
   */
  private Post createTestPost() {
    createTestUser();

    Post post = Post.newTestPost(
        TEST_POST_ID,
        TEST_USER_ID,
        TEST_POST_CONTENT,
        0,
        PostStatus.ACTIVE
    );

    return postPersistenceAdapter.savePost(post);
  }

  @Order(1)
  @DisplayName("게시글 저장 테스트")
  @Nested
  class SavePostTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 게시글 저장 시 postId가 정상적으로 반환된다")
      @Test
      void savePost_shouldReturnPostId_whenPostIsValid() {
        //given
        createTestUser();
        String postId = "new-post-id";
        String content = "새로운 게시글 내용";
        Post post = Post.newTestPost(
            postId,
            TEST_USER_ID,
            content,
            0,
            PostStatus.ACTIVE
        );

        //when
        Post savedPost = postPersistenceAdapter.savePost(post);

        //then
        assertThat(savedPost).isNotNull();
        assertThat(savedPost.getPostId()).isEqualTo(postId);
        assertThat(savedPost.getContent()).isEqualTo(content);
        assertThat(savedPost.getUserId()).isEqualTo(TEST_USER_ID);
      }
    }
  }

  @Order(2)
  @DisplayName("게시글 ID로 조회 테스트")
  @Nested
  class FetchByIdTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 존재하는 postId로 조회 시 게시글이 반환된다")
      @Test
      void fetchById_shouldReturnPost_whenPostIdExists() {
        //given
        createTestPost();
        String postId = TEST_POST_ID;

        //when
        Optional<Post> result = postPersistenceAdapter.fetchById(postId);

        //then
        assertThat(result).isPresent();
        assertThat(result.get().getPostId()).isEqualTo(postId);
        assertThat(result.get().getContent()).isEqualTo(TEST_POST_CONTENT);
        assertThat(result.get().getUserId()).isEqualTo(TEST_USER_ID);
      }
    }

    @DisplayName("실패")
    @Nested
    class Fail {

      @DisplayName("1. 존재하지 않는 postId로 조회 시 빈 Optional이 반환된다")
      @Test
      void fetchById_shouldReturnEmpty_whenPostIdDoesNotExist() {
        //given
        String nonExistentPostId = "non-existent-post-id";

        //when
        Optional<Post> result = postPersistenceAdapter.fetchById(nonExistentPostId);

        //then
        assertThat(result).isEmpty();
      }
    }
  }

  @Order(3)
  @DisplayName("게시글 수정 테스트")
  @Nested
  class UpdatePostTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 게시글 내용이 정상적으로 수정된다")
      @Test
      void updatePost_shouldUpdateContent_whenPostExists() {
        //given
        Post post = createTestPost();
        String modifiedContent = "수정된 게시글 내용입니다";
        post = Post.newTestPost(
            post.getPostId(),
            post.getUserId(),
            modifiedContent,
            post.getViewCount(),
            post.getStatus()
        );

        //when
        postPersistenceAdapter.updatePost(post);
        entityManager.flush();
        entityManager.clear();

        //then
        Optional<Post> result = postPersistenceAdapter.fetchById(TEST_POST_ID);
        assertThat(result).isPresent();
        assertThat(result.get().getContent()).isEqualTo(modifiedContent);
      }
    }
  }

  @Order(4)
  @DisplayName("게시글 상태 변경 테스트")
  @Nested
  class ChangeStatusTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 게시글 상태가 DELETED로 정상적으로 변경된다")
      @Test
      void changeStatus_shouldUpdateStatus_whenPostExists() {
        //given
        Post post = createTestPost();
        PostStatus newStatus = PostStatus.DELETED;
        post = Post.newTestPost(
            post.getPostId(),
            post.getUserId(),
            post.getContent(),
            post.getViewCount(),
            newStatus
        );

        //when
        postPersistenceAdapter.changeStatus(post);
        entityManager.flush();
        entityManager.clear();

        //then
        Optional<PostStatus> result = postPersistenceAdapter.fetchPostStatusByPostId(TEST_POST_ID);
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(newStatus);
      }
    }
  }

  @Order(5)
  @DisplayName("게시글 존재 여부 확인 테스트")
  @Nested
  class ExistsByIdTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 존재하는 게시글의 경우 true를 반환한다")
      @Test
      void existsById_shouldReturnTrue_whenPostExists() {
        //given
        createTestPost();
        String postId = TEST_POST_ID;

        //when
        boolean exists = postPersistenceAdapter.existsById(postId);

        //then
        assertThat(exists).isTrue();
      }

      @DisplayName("2. 존재하지 않는 게시글의 경우 false를 반환한다")
      @Test
      void existsById_shouldReturnFalse_whenPostDoesNotExist() {
        //given
        String nonExistentPostId = "non-existent-post-id";

        //when
        boolean exists = postPersistenceAdapter.existsById(nonExistentPostId);

        //then
        assertThat(exists).isFalse();
      }
    }
  }

  @Order(6)
  @DisplayName("게시글 조회수 증가 테스트")
  @Nested
  class IncreaseViewCountTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 조회수가 1 증가한다")
      @Test
      void increaseViewCount_shouldIncreaseByOne_whenPostExists() {
        //given
        Post post = createTestPost();
        String postId = TEST_POST_ID;
        int initialViewCount = post.getViewCount();

        //when
        postPersistenceAdapter.increaseViewCount(postId);
        entityManager.flush();
        entityManager.clear();

        //then
        Optional<Post> result = postPersistenceAdapter.fetchById(postId);
        assertThat(result).isPresent();
        assertThat(result.get().getViewCount()).isEqualTo(initialViewCount + 1);
      }
    }
  }

  @Order(7)
  @DisplayName("게시글 작성자 조회 테스트")
  @Nested
  class FetchUserIdByPostIdTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 게시글 ID로 작성자 userId를 조회할 수 있다")
      @Test
      void fetchUserIdByPostId_shouldReturnUserId_whenPostExists() {
        //given
        createTestPost();
        String postId = TEST_POST_ID;

        //when
        Optional<String> result = postPersistenceAdapter.fetchUserIdByPostId(postId);

        //then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(TEST_USER_ID);
      }
    }

    @DisplayName("실패")
    @Nested
    class Fail {

      @DisplayName("1. 존재하지 않는 게시글의 경우 빈 Optional이 반환된다")
      @Test
      void fetchUserIdByPostId_shouldReturnEmpty_whenPostDoesNotExist() {
        //given
        String nonExistentPostId = "non-existent-post-id";

        //when
        Optional<String> result = postPersistenceAdapter.fetchUserIdByPostId(nonExistentPostId);

        //then
        assertThat(result).isEmpty();
      }
    }
  }
}
