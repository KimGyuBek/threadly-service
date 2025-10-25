package com.threadly.search.user;

import static com.threadly.utils.TestConstants.EMAIL_VERIFIED_USER_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.domain.follow.FollowStatus;
import com.threadly.core.port.user.in.search.dto.UserSearchItem;
import com.threadly.testsupport.fixture.users.UserFollowFixtureLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 사용자 검색 관련 API 테스트
 */
@DisplayName("사용자 닉네임 검색 관련 API 테스트")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class UserSearchApiTest extends BaseUserSearchApiTest {

  @Autowired
  private UserFollowFixtureLoader userFollowFixtureLoader;

  // 검색 키워드
  public static final String KEYWORD_SINGLE = "고유한닉네임";  // 단건 검색용
  public static final String KEYWORD_MULTI = "검색유저";  // 복수건 검색용 (5건)
  public static final String KEYWORD_NOT_EXIST = "존재하지않는닉네임987654321";
  public static final String KEYWORD_FOLLOWING = "팔로잉유저";
  public static final String KEYWORD_NOT_FOLLOWING = "팔로잉안하는유저";

  @BeforeEach
  void setUp() {
    // 기본 검색 테스트용 데이터 로드
    userFixtureLoader.load("/users/search/base-users.json");
  }

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    /*[Case #1] 단건 검색 - 고유 닉네임으로 1건만 검색*/
    @Order(1)
    @DisplayName("1. 정상적인 사용자 닉네임 검색 결과 단건 검증")
    @Test
    public void searchUser_shouldReturnSingleResult_whenSearchWithUniqueKeyword()
        throws Exception {
      //given
      userFixtureLoader.load("/users/search/single-search-user.json");
      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      //when
      //then
      CommonResponse<CursorPageApiResponse<UserSearchItem>> searchResponse =
          sendUserSearchRequest(accessToken, KEYWORD_SINGLE, 10, status().isOk());

      assertThat(searchResponse.getData().content()).hasSize(1);
      assertThat(searchResponse.getData().content().getFirst().user().nickname())
          .contains(KEYWORD_SINGLE);
    }

    /*[Case #2] 복수건 검색 - 여러 건 검색*/
    @Order(2)
    @DisplayName("2. keyword 검색 후 검색 결과 리스트 검증")
    @Test
    public void searchUser_shouldReturnMultipleResults_whenSearchWithCommonKeyword()
        throws Exception {
      //given
      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      //when
      //then
      CommonResponse<CursorPageApiResponse<UserSearchItem>> searchResponse =
          sendUserSearchRequest(accessToken, KEYWORD_MULTI, 20, status().isOk());

      assertThat(searchResponse.getData().content().size()).isEqualTo(5);
      // 모든 결과에 키워드가 포함되어 있는지 확인
      searchResponse.getData().content().forEach(user ->
          assertThat(user.user().nickname()).contains(KEYWORD_MULTI)
      );
    }

    /*[Case #3] 존재하지 않는 닉네임 검색 - 빈 리스트*/
    @Order(3)
    @DisplayName("3. 존재하지 않는 닉네임에 대한 검색 결과 검증")
    @Test
    public void searchUser_shouldReturnEmptyList_whenSearchWithNonExistentKeyword()
        throws Exception {
      //given
      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      //when
      //then
      CommonResponse<CursorPageApiResponse<UserSearchItem>> searchResponse =
          sendUserSearchRequest(accessToken, KEYWORD_NOT_EXIST, 10, status().isOk());

      assertThat(searchResponse.getData().content()).isEmpty();
    }

    /*[Case #4] 팔로잉하는 사용자 검색 - FollowStatus.APPROVED 검증*/
    @Order(4)
    @DisplayName("4. 팔로잉하는 사용자 검색 시 응답의 followStatus가 APPROVED인지 검증")
    @Test
    public void searchUser_shouldReturnApprovedStatus_whenSearchingFollowingUser()
        throws Exception {
      //given
      // 팔로우 관계 설정
      userFollowFixtureLoader.load(
          "/users/search/following-user.json",
          "/users/search/follow-relation.json"
      );

      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      //when
      //then
      CommonResponse<CursorPageApiResponse<UserSearchItem>> searchResponse =
          sendUserSearchRequest(accessToken, KEYWORD_FOLLOWING, 10, status().isOk());

      assertThat(searchResponse.getData().content()).hasSize(1);
      assertThat(searchResponse.getData().content().getFirst().followStatus())
          .isEqualTo(FollowStatus.APPROVED);
    }

    /*[Case #5] 팔로잉하지 않는 사용자 검색 - FollowStatus.NONE 검증*/
    @Order(5)
    @DisplayName("5. 팔로잉하지 않는 사용자 검색 시 응답의 followStatus가 NONE인지 검증")
    @Test
    public void searchUser_shouldReturnNoneStatus_whenSearchingNotFollowingUser()
        throws Exception {
      //given
      userFixtureLoader.load("/users/search/not-following-user.json");
      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      //when
      //then
      CommonResponse<CursorPageApiResponse<UserSearchItem>> searchResponse =
          sendUserSearchRequest(accessToken, KEYWORD_NOT_FOLLOWING, 10, status().isOk());

      assertThat(searchResponse.getData().content()).hasSize(1);
      assertThat(searchResponse.getData().content().getFirst().followStatus())
          .isEqualTo(FollowStatus.NONE);
    }

    /*[Case #6] 내 프로필 검색 - FollowStatus.SELF 검증*/
    @Order(6)
    @DisplayName("6. 내 프로필 검색 시 응답의 followStatus가 SELF인지 검증")
    @Test
    public void searchUser_shouldReturnSelfStatus_whenSearchingMyProfile() throws Exception {
      //given
      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      // usr1의 닉네임: "sunset_gazer"
      String myNickname = "sunset_gazer";

      //when
      CommonResponse<CursorPageApiResponse<UserSearchItem>> searchResponse =
          sendUserSearchRequest(accessToken, myNickname, 10, status().isOk());

      //then
      assertThat(searchResponse.getData().content()).hasSize(1);
      assertThat(searchResponse.getData().content().getFirst().followStatus())
          .isEqualTo(FollowStatus.SELF);
      assertThat(searchResponse.getData().content().getFirst().user().userId())
          .isEqualTo("usr1");
    }

    /*[Case #7] 커서 기반 페이징 - 전체 순회*/
    @Order(7)
    @DisplayName("7. 커서 기반 페이징으로 전체 검색 결과를 마지막 페이지까지 순회하며 조회")
    @Test
    public void searchUser_shouldIterateAllPages_usingCursorPagination() throws Exception {
      //given
      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      //when
      //then
      String cursorId = null;
      int limit = 2;
      int totalSize = 0;

      while (true) {
        CommonResponse<CursorPageApiResponse<UserSearchItem>> searchResponse =
            sendUserSearchRequest(
                accessToken,
                KEYWORD_MULTI,
                cursorId,
                limit,
                status().isOk()
            );

        totalSize += searchResponse.getData().content().size();

        /*마지막 페이지인 경우*/
        if (searchResponse.getData().nextCursor().cursorId() == null) {
          break;
        }

        cursorId = searchResponse.getData().nextCursor().cursorId();
      }

      assertThat(totalSize).isEqualTo(5);
    }
  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

    /*[Fail Case #1] keyword 없이 검색*/
    @Order(1)
    @DisplayName("1. keyword 없이 검색 요청 시 전체 사용자 반환")
    @Test
    public void searchUser_shouldReturnAllUsers_whenKeywordIsNull() throws Exception {
      //given
      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      //when
      //then
      CommonResponse<CursorPageApiResponse<UserSearchItem>> searchResponse =
          sendUserSearchRequest(accessToken, null, 10, status().isOk());

      // keyword가 없으면 전체 사용자 반환
      assertThat(searchResponse.getData().content()).isNotEmpty();
    }
  }
}
