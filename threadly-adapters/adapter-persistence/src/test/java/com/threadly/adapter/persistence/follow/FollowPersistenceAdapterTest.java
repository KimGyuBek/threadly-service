package com.threadly.adapter.persistence.follow;

import static org.assertj.core.api.Assertions.assertThat;

import com.threadly.BasePersistenceTest;
import com.threadly.adapter.persistence.follow.adapter.FollowPersistenceAdapter;
import com.threadly.core.domain.follow.Follow;
import com.threadly.core.domain.follow.FollowStatus;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.ClassOrderer;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * FollowPersistenceAdapter 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class FollowPersistenceAdapterTest extends BasePersistenceTest {

  @Autowired
  private FollowPersistenceAdapter followPersistenceAdapter;

  private static final String TEST_FOLLOWER_ID = "test-follower-id";
  private static final String TEST_FOLLOWING_ID = "test-following-id";
  private static final String TEST_FOLLOW_ID = "test-follow-id-1";

  /**
   * 테스트용 팔로우 생성
   */
  private Follow createTestFollow() {
    createUser(TEST_FOLLOWER_ID, "follower@example.com", "팔로워");
    createUser(TEST_FOLLOWING_ID, "following@example.com", "팔로잉");

    Follow follow = Follow.builder()
        .followId(TEST_FOLLOW_ID)
        .followerId(TEST_FOLLOWER_ID)
        .followingId(TEST_FOLLOWING_ID)
        .statusType(FollowStatus.PENDING)
        .build();

    followPersistenceAdapter.createFollow(follow);
    entityManager.flush();
    entityManager.clear();
    return follow;
  }

  @Order(1)
  @DisplayName("팔로우 생성 테스트")
  @Nested
  class CreateFollowTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 팔로우가 정상적으로 생성된다")
      @Test
      void createFollow_shouldCreateFollow_whenValid() {
        //given
        createUser(TEST_FOLLOWER_ID, "follower@example.com", "팔로워");
        createUser(TEST_FOLLOWING_ID, "following@example.com", "팔로잉");

        String followId = "new-follow-id";
        Follow follow = Follow.builder()
            .followId(followId)
            .followerId(TEST_FOLLOWER_ID)
            .followingId(TEST_FOLLOWING_ID)
            .statusType(FollowStatus.PENDING)
            .build();

        //when
        followPersistenceAdapter.createFollow(follow);
        entityManager.flush();
        entityManager.clear();

        //then
        Optional<FollowStatus> status = followPersistenceAdapter.findFollowStatusType(
            TEST_FOLLOWER_ID, TEST_FOLLOWING_ID);
        assertThat(status).isPresent();
        assertThat(status.get()).isEqualTo(FollowStatus.PENDING);
      }
    }
  }

  @Order(2)
  @DisplayName("팔로우 여부 확인 테스트")
  @Nested
  class IsFollowingTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 팔로우 관계가 있으면 true를 반환한다")
      @Test
      void isFollowing_shouldReturnTrue_whenFollowExists() {
        //given
        createTestFollow();

        //when
        boolean isFollowing = followPersistenceAdapter.isFollowing(TEST_FOLLOWER_ID,
            TEST_FOLLOWING_ID);

        //then
        assertThat(isFollowing).isTrue();
      }

      @DisplayName("2. 팔로우 관계가 없으면 false를 반환한다")
      @Test
      void isFollowing_shouldReturnFalse_whenFollowDoesNotExist() {
        //given
        createUser(TEST_FOLLOWER_ID, "follower@example.com", "팔로워");
        createUser(TEST_FOLLOWING_ID, "following@example.com", "팔로잉");

        //when
        boolean isFollowing = followPersistenceAdapter.isFollowing(TEST_FOLLOWER_ID,
            TEST_FOLLOWING_ID);

        //then
        assertThat(isFollowing).isFalse();
      }
    }
  }

  @Order(3)
  @DisplayName("팔로우 상태 조회 테스트")
  @Nested
  class FindFollowStatusTypeTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 팔로우 상태가 정상적으로 조회된다")
      @Test
      void findFollowStatusType_shouldReturnStatus_whenFollowExists() {
        //given
        createTestFollow();

        //when
        Optional<FollowStatus> status = followPersistenceAdapter.findFollowStatusType(
            TEST_FOLLOWER_ID, TEST_FOLLOWING_ID);

        //then
        assertThat(status).isPresent();
        assertThat(status.get()).isEqualTo(FollowStatus.PENDING);
      }
    }

    @DisplayName("실패")
    @Nested
    class Fail {

      @DisplayName("1. 팔로우 관계가 없으면 빈 Optional이 반환된다")
      @Test
      void findFollowStatusType_shouldReturnEmpty_whenFollowDoesNotExist() {
        //given
        createUser(TEST_FOLLOWER_ID, "follower@example.com", "팔로워");
        createUser(TEST_FOLLOWING_ID, "following@example.com", "팔로잉");

        //when
        Optional<FollowStatus> status = followPersistenceAdapter.findFollowStatusType(
            TEST_FOLLOWER_ID, TEST_FOLLOWING_ID);

        //then
        assertThat(status).isEmpty();
      }
    }
  }

  @Order(4)
  @DisplayName("팔로우 상태 업데이트 테스트")
  @Nested
  class UpdateFollowStatusTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 팔로우 상태가 APPROVED로 정상적으로 변경된다")
      @Test
      void updateFollowStatus_shouldUpdateStatus_whenFollowExists() {
        //given
        Follow follow = createTestFollow();
        follow = Follow.builder()
            .followId(follow.getFollowId())
            .followerId(follow.getFollowerId())
            .followingId(follow.getFollowingId())
            .statusType(FollowStatus.APPROVED)
            .build();

        //when
        followPersistenceAdapter.updateFollowStatus(follow);
        entityManager.flush();
        entityManager.clear();

        //then
        Optional<FollowStatus> status = followPersistenceAdapter.findFollowStatusType(
            TEST_FOLLOWER_ID, TEST_FOLLOWING_ID);
        assertThat(status).isPresent();
        assertThat(status.get()).isEqualTo(FollowStatus.APPROVED);
      }
    }
  }

  @Order(5)
  @DisplayName("팔로우 삭제 테스트")
  @Nested
  class DeleteFollowTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 팔로우가 정상적으로 삭제된다")
      @Test
      void deleteFollow_shouldDelete_whenFollowExists() {
        //given
        Follow follow = createTestFollow();
        String followId = follow.getFollowId();

        //when
        followPersistenceAdapter.deleteFollow(followId);
        entityManager.flush();
        entityManager.clear();

        //then
        Optional<FollowStatus> status = followPersistenceAdapter.findFollowStatusType(
            TEST_FOLLOWER_ID, TEST_FOLLOWING_ID);
        assertThat(status).isEmpty();
      }
    }
  }

  @Order(6)
  @DisplayName("특정 상태의 팔로우 존재 여부 확인 테스트")
  @Nested
  class ExistsByFollowerIdAndFollowingIdAndStatusTypeTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 특정 상태의 팔로우가 존재하면 true를 반환한다")
      @Test
      void existsByFollowerIdAndFollowingIdAndStatusType_shouldReturnTrue_whenExists() {
        //given
        createTestFollow();

        //when
        boolean exists = followPersistenceAdapter.existsByFollowerIdAndFollowingIdAndStatusType(
            TEST_FOLLOWER_ID, TEST_FOLLOWING_ID, FollowStatus.PENDING);

        //then
        assertThat(exists).isTrue();
      }

      @DisplayName("2. 특정 상태의 팔로우가 존재하지 않으면 false를 반환한다")
      @Test
      void existsByFollowerIdAndFollowingIdAndStatusType_shouldReturnFalse_whenNotExists() {
        //given
        createTestFollow();

        //when
        boolean exists = followPersistenceAdapter.existsByFollowerIdAndFollowingIdAndStatusType(
            TEST_FOLLOWER_ID, TEST_FOLLOWING_ID, FollowStatus.APPROVED);

        //then
        assertThat(exists).isFalse();
      }
    }
  }

  @Order(7)
  @DisplayName("특정 상태의 팔로우 삭제 테스트")
  @Nested
  class DeleteByFollowerIdAndFollowingIdAndStatusTypeTest {

    @DisplayName("성공")
    @Nested
    class Success {

      @DisplayName("1. 특정 상태의 팔로우가 정상적으로 삭제된다")
      @Test
      void deleteByFollowerIdAndFollowingIdAndStatusType_shouldDelete_whenExists() {
        //given
        createTestFollow();

        //when
        followPersistenceAdapter.deleteByFollowerIdAndFollowingIdAndStatusType(
            TEST_FOLLOWER_ID, TEST_FOLLOWING_ID, FollowStatus.PENDING);
        entityManager.flush();
        entityManager.clear();

        //then
        boolean exists = followPersistenceAdapter.existsByFollowerIdAndFollowingIdAndStatusType(
            TEST_FOLLOWER_ID, TEST_FOLLOWING_ID, FollowStatus.PENDING);
        assertThat(exists).isFalse();
      }
    }
  }
}
