package com.threadly.core.service.user.search;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.domain.follow.FollowStatus;
import com.threadly.core.port.user.in.search.dto.UserSearchItem;
import com.threadly.core.port.user.in.search.dto.UserSearchQuery;
import com.threadly.core.port.user.out.search.SearchUserQueryPort;
import com.threadly.core.port.user.out.search.UserSearchProjection;
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
 * SearchUserQueryService 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class SearchUserQueryServiceTest {

  @InjectMocks
  private SearchUserQueryService searchUserQueryService;

  @Mock
  private SearchUserQueryPort searchUserQueryPort;

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("사용자 검색")
  class SearchTest {

    /*[Case #1] 사용자 검색 결과 조회*/
    @Order(1)
    @DisplayName("1. 사용자 검색 결과가 커서 기반으로 조회되는지 검증")
    @Test
    void searchByKeyword_shouldReturnPagedResponse() throws Exception {
      //given
      UserSearchQuery query = new UserSearchQuery("viewer-1", "thread", "cursor", 1);

      UserSearchProjection projection1 = new UserSearchProjection() {
        @Override
        public String getUserId() {
          return "target-1";
        }

        @Override
        public String getUserNickname() {
          return "타겟1";
        }

        @Override
        public String getUserProfileImageUrl() {
          return null;
        }

        @Override
        public FollowStatus getFollowStatus() {
          return FollowStatus.APPROVED;
        }
      };

      UserSearchProjection projection2 = new UserSearchProjection() {
        @Override
        public String getUserId() {
          return "viewer-1";
        }

        @Override
        public String getUserNickname() {
          return "나";
        }

        @Override
        public String getUserProfileImageUrl() {
          return "/me.png";
        }

        @Override
        public FollowStatus getFollowStatus() {
          return FollowStatus.APPROVED;
        }
      };

      when(searchUserQueryPort.searchByKeyword(
          query.userId(),
          query.keyword(),
          query.cursorNickname(),
          query.limit() + 1
      )).thenReturn(List.of(projection1, projection2));

      //when
      CursorPageApiResponse<UserSearchItem> response =
          searchUserQueryService.searchByKeyword(query);

      //then
      verify(searchUserQueryPort).searchByKeyword(
          query.userId(),
          query.keyword(),
          query.cursorNickname(),
          query.limit() + 1
      );

      assertThat(response.content()).hasSize(1);
      UserSearchItem first = response.content().getFirst();
      assertThat(first.user().userId()).isEqualTo("target-1");
      assertThat(first.user().profileImageUrl()).isEqualTo("default");
      assertThat(first.followStatus()).isEqualTo(FollowStatus.APPROVED);
    }

    /*[Case #2] 검색 결과에 본인이 포함된 경우*/
    @Order(2)
    @DisplayName("2. 검색 결과에 본인이 포함되면 FOLLOW_STATUS가 SELF로 설정되는지 검증")
    @Test
    void searchByKeyword_shouldMarkSelf_whenUserIncluded() throws Exception {
      //given
      UserSearchQuery query = new UserSearchQuery("viewer-1", "thread", null, 10);

      UserSearchProjection projection = new UserSearchProjection() {
        @Override
        public String getUserId() {
          return "viewer-1";
        }

        @Override
        public String getUserNickname() {
          return "나";
        }

        @Override
        public String getUserProfileImageUrl() {
          return null;
        }

        @Override
        public FollowStatus getFollowStatus() {
          return FollowStatus.APPROVED;
        }
      };

      when(searchUserQueryPort.searchByKeyword(
          query.userId(),
          query.keyword(),
          query.cursorNickname(),
          query.limit() + 1
      )).thenReturn(List.of(projection));

      //when
      CursorPageApiResponse<UserSearchItem> response =
          searchUserQueryService.searchByKeyword(query);

      //then
      UserSearchItem item = response.content().getFirst();
      assertThat(item.followStatus()).isEqualTo(FollowStatus.SELF);
    }
  }
}
