package com.threadly.core.service.follow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.domain.follow.FollowStatus;
import com.threadly.core.port.follow.in.query.dto.FollowRequestResponse;
import com.threadly.core.port.follow.in.query.dto.GetFollowRequestsQuery;
import com.threadly.core.port.follow.in.query.dto.GetFollowersQuery;
import com.threadly.core.port.follow.in.query.dto.GetFollowingsQuery;
import com.threadly.core.port.follow.in.query.dto.GetUserFollowStatsApiResponse;
import com.threadly.core.port.follow.in.query.dto.FollowerResponse;
import com.threadly.core.port.follow.in.query.dto.FollowingApiResponse;
import com.threadly.core.port.follow.out.FollowQueryPort;
import com.threadly.core.port.follow.out.projection.FollowRequestsProjection;
import com.threadly.core.port.follow.out.projection.FollowerProjection;
import com.threadly.core.port.follow.out.projection.FollowingProjection;
import com.threadly.core.port.follow.out.projection.UserFollowStatsProjection;
import com.threadly.core.service.validator.follow.FollowAccessValidator;
import com.threadly.core.service.validator.user.UserValidator;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * FollowQueryService 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class FollowQueryServiceTest {

  @InjectMocks
  private FollowQueryService followQueryService;

  @Mock
  private FollowQueryPort followQueryPort;

  @Mock
  private FollowAccessValidator followAccessValidator;

  @Mock
  private UserValidator userValidator;

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("팔로우 요청 목록 조회")
  class GetFollowRequestsTest {

    /*[Case #1] 팔로우 요청 커서 기반 조회*/
    @Order(1)
    @DisplayName("1. 팔로우 요청 목록이 커서 기반으로 조회되는지 검증")
    @Test
    void getFollowRequestsByCursor_shouldReturnPagedResponse() throws Exception {
      //given
      LocalDateTime now = LocalDateTime.now();
      GetFollowRequestsQuery query = new GetFollowRequestsQuery("user-1", now, "cursor-1", 1);

      FollowRequestsProjection projection1 = new FollowRequestsProjection() {
        @Override
        public String getFollowId() {
          return "follow-1";
        }

        @Override
        public String getRequesterId() {
          return "requester-1";
        }

        @Override
        public String getRequesterNickname() {
          return "요청자1";
        }

        @Override
        public String getRequesterProfileImageUrl() {
          return null;
        }

        @Override
        public LocalDateTime getFollowRequestedAt() {
          return now.minusSeconds(1);
        }
      };

      FollowRequestsProjection projection2 = new FollowRequestsProjection() {
        @Override
        public String getFollowId() {
          return "follow-2";
        }

        @Override
        public String getRequesterId() {
          return "requester-2";
        }

        @Override
        public String getRequesterNickname() {
          return "요청자2";
        }

        @Override
        public String getRequesterProfileImageUrl() {
          return "/requester-2.png";
        }

        @Override
        public LocalDateTime getFollowRequestedAt() {
          return now.minusSeconds(2);
        }
      };

      when(followQueryPort.findFollowRequestsByCursor(
          query.userId(),
          query.cursorTimestamp(),
          query.cursorId(),
          query.limit() + 1
      )).thenReturn(List.of(projection1, projection2));

      //when
      CursorPageApiResponse<FollowRequestResponse> response =
          followQueryService.getFollowRequestsByCursor(query);

      //then
      verify(followQueryPort).findFollowRequestsByCursor(
          query.userId(),
          query.cursorTimestamp(),
          query.cursorId(),
          query.limit() + 1
      );

      assertThat(response.content()).hasSize(1);
      FollowRequestResponse first = response.content().getFirst();
      assertThat(first.followId()).isEqualTo("follow-1");
      assertThat(first.requester().userId()).isEqualTo("requester-1");
      assertThat(first.requester().profileImageUrl()).isEqualTo("/");
      assertThat(first.followRequestedAt()).isEqualTo(projection1.getFollowRequestedAt());
    }
  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("팔로워 목록 조회")
  class GetFollowersTest {

    /*[Case #1] 팔로워 커서 기반 조회*/
    @Order(1)
    @DisplayName("1. 팔로워 목록이 커서 기반으로 조회되는지 검증")
    @Test
    void getFollowers_shouldReturnPagedResponse() throws Exception {
      //given
      LocalDateTime now = LocalDateTime.now();
      GetFollowersQuery query = new GetFollowersQuery("user-1", "target-1", now, "cursor-1", 1);

      FollowerProjection projection1 = new FollowerProjection() {
        @Override
        public String getFollowerId() {
          return "follower-1";
        }

        @Override
        public String getFollowerNickname() {
          return "팔로워1";
        }

        @Override
        public String getFollowerProfileImageUrl() {
          return null;
        }

        @Override
        public LocalDateTime getFollowedAt() {
          return now.minusSeconds(1);
        }
      };

      FollowerProjection projection2 = new FollowerProjection() {
        @Override
        public String getFollowerId() {
          return "follower-2";
        }

        @Override
        public String getFollowerNickname() {
          return "팔로워2";
        }

        @Override
        public String getFollowerProfileImageUrl() {
          return "/follower-2.png";
        }

        @Override
        public LocalDateTime getFollowedAt() {
          return now.minusSeconds(2);
        }
      };

      when(followAccessValidator.validateProfileAccessibleWithException(
          query.userId(), query.targetUserId())).thenReturn(FollowStatus.APPROVED);
      when(followQueryPort.findFollowersByCursor(
          query.targetUserId(),
          query.cursorTimestamp(),
          query.cursorId(),
          query.limit() + 1
      )).thenReturn(List.of(projection1, projection2));

      //when
      CursorPageApiResponse<FollowerResponse> response = followQueryService.getFollowers(query);

      //then
      verify(followAccessValidator)
          .validateProfileAccessibleWithException(query.userId(), query.targetUserId());
      verify(followQueryPort).findFollowersByCursor(
          query.targetUserId(),
          query.cursorTimestamp(),
          query.cursorId(),
          query.limit() + 1
      );

      assertThat(response.content()).hasSize(1);
      FollowerResponse first = response.content().getFirst();
      assertThat(first.follower().userId()).isEqualTo("follower-1");
      assertThat(first.follower().profileImageUrl()).isEqualTo("/");
      assertThat(first.followedAt()).isEqualTo(projection1.getFollowedAt());
    }
  }

  @Order(3)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("팔로잉 목록 조회")
  class GetFollowingsTest {

    /*[Case #1] 팔로잉 커서 기반 조회*/
    @Order(1)
    @DisplayName("1. 팔로잉 목록이 커서 기반으로 조회되는지 검증")
    @Test
    void getFollowings_shouldReturnPagedResponse() throws Exception {
      //given
      LocalDateTime now = LocalDateTime.now();
      GetFollowingsQuery query = new GetFollowingsQuery("user-1", "target-1", now, "cursor-1", 1);

      FollowingProjection projection1 = new FollowingProjection() {
        @Override
        public String getFollowingId() {
          return "following-1";
        }

        @Override
        public String getFollowingNickname() {
          return "팔로잉1";
        }

        @Override
        public String getFollowingProfileImageUrl() {
          return null;
        }

        @Override
        public LocalDateTime getFollowedAt() {
          return now.minusSeconds(1);
        }
      };

      FollowingProjection projection2 = new FollowingProjection() {
        @Override
        public String getFollowingId() {
          return "following-2";
        }

        @Override
        public String getFollowingNickname() {
          return "팔로잉2";
        }

        @Override
        public String getFollowingProfileImageUrl() {
          return "/following-2.png";
        }

        @Override
        public LocalDateTime getFollowedAt() {
          return now.minusSeconds(2);
        }
      };

      when(followAccessValidator.validateProfileAccessibleWithException(
          query.userId(), query.targetUserId())).thenReturn(FollowStatus.APPROVED);
      when(followQueryPort.findFollowingsByCursor(
          query.targetUserId(),
          query.cursorTimestamp(),
          query.cursorId(),
          query.limit() + 1
      )).thenReturn(List.of(projection1, projection2));

      //when
      CursorPageApiResponse<FollowingApiResponse> response = followQueryService.getFollowings(query);

      //then
      verify(followAccessValidator)
          .validateProfileAccessibleWithException(query.userId(), query.targetUserId());
      verify(followQueryPort).findFollowingsByCursor(
          query.targetUserId(),
          query.cursorTimestamp(),
          query.cursorId(),
          query.limit() + 1
      );

      assertThat(response.content()).hasSize(1);
      FollowingApiResponse first = response.content().getFirst();
      assertThat(first.following().userId()).isEqualTo("following-1");
      assertThat(first.following().profileImageUrl()).isEqualTo("/");
      assertThat(first.followingAt()).isEqualTo(projection1.getFollowedAt());
    }
  }

  @Order(4)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("팔로우 통계 조회")
  class GetUserFollowStatsTest {

    /*[Case #1] 사용자 팔로우 통계 조회*/
    @Order(1)
    @DisplayName("1. 사용자 팔로우 통계가 조회되는지 검증")
    @Test
    void getUserFollowStats_shouldReturnStats() throws Exception {
      //given
      String userId = "user-1";
      UserFollowStatsProjection projection = new UserFollowStatsProjection() {
        @Override
        public int getFollowerCount() {
          return 3;
        }

        @Override
        public int getFollowingCount() {
          return 5;
        }
      };

      when(followQueryPort.getUserFollowStatusByUserId(userId)).thenReturn(projection);

      //when
      GetUserFollowStatsApiResponse response = followQueryService.getUserFollowStats(userId);

      //then
      verify(userValidator).validateUserStatusWithException(userId);
      verify(followQueryPort).getUserFollowStatusByUserId(userId);
      assertThat(response.followerCount()).isEqualTo(3);
      assertThat(response.followingCount()).isEqualTo(5);
    }
  }
}
