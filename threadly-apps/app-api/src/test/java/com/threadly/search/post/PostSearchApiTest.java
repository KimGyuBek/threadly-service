package com.threadly.search.post;

import static com.threadly.utils.TestConstants.EMAIL_VERIFIED_USER_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.domain.user.UserStatus;
import com.threadly.core.port.post.in.command.dto.CreatePostApiResponse;
import com.threadly.core.port.post.in.search.dto.PostSearchSortType;
import com.threadly.core.port.post.in.search.dto.PostSearchItem;
import com.threadly.testsupport.fixture.posts.PostFixtureLoader;
import com.threadly.testsupport.fixture.users.UserFollowFixtureLoader;
import java.time.LocalDateTime;
import java.util.List;
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
 * 게시글 검색 API 테스트
 */
@DisplayName("게시글 검색 기능 API 테스트")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class PostSearchApiTest extends BasePostSearchApiTest {

  @Autowired
  private PostFixtureLoader postFixtureLoader;

  @Autowired
  private UserFollowFixtureLoader userFollowFixtureLoader;

  // 검색 키워드
  public static final String KEYWORD_SINGLE = "감사합니다";  // 단건 검색용
  public static final String KEYWORD_MULTI = "좋";  // 복수건 검색용 (14건)
  public static final String KEYWORD_NOT_EXIST = "존재하지않는키워드987654321";
  public static final String KEYWORD_PUBLIC = "공개계정테스트게시글";
  public static final String KEYWORD_PRIVATE = "비공개계정테스트게시글";

  @BeforeEach
  void setUp() {
    // 기본 검색 테스트용 데이터 로드
    postFixtureLoader.load("/posts/search/base-users.json", "/posts/search/base-posts.json");
  }

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    /*[Case #1] 단건 검색 - 고유 키워드로 1건만 검색*/
    @Order(1)
    @DisplayName("1. 정상적인 게시글 검색 결과 단건 검증")
    @Test
    public void searchPost_shouldReturnSingleResult_whenSearchWithUniqueKeyword()
        throws Exception {
      //given
      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      //when
      //then
      CommonResponse<CursorPageApiResponse<PostSearchItem>> searchResponse =
          sendPostSearchRequest(accessToken, KEYWORD_SINGLE, 10, status().isOk());

      assertThat(searchResponse.getData().content()).hasSize(1);
      assertThat(searchResponse.getData().content().getFirst().content())
          .contains(KEYWORD_SINGLE);
    }

    /*[Case #2] 복수건 검색 - 여러 건 검색*/
    @Order(2)
    @DisplayName("2. keyword 검색 후 검색 결과 리스트 검증")
    @Test
    public void searchPost_shouldReturnMultipleResults_whenSearchWithCommonKeyword()
        throws Exception {
      //given
      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      //when
      //then
      CommonResponse<CursorPageApiResponse<PostSearchItem>> searchResponse =
          sendPostSearchRequest(accessToken, KEYWORD_MULTI, 20, status().isOk());

      assertThat(searchResponse.getData().content().size()).isEqualTo(14);
      // 모든 결과에 키워드가 포함되어 있는지 확인
      searchResponse.getData().content().forEach(post ->
          assertThat(post.content()).contains(KEYWORD_MULTI)
      );
    }

    /*[Case #3] 존재하지 않는 키워드 검색 - 빈 리스트*/
    @Order(3)
    @DisplayName("3. 존재하지 않는 데이터에 대한 검색 결과 검증")
    @Test
    public void searchPost_shouldReturnEmptyList_whenSearchWithNonExistentKeyword()
        throws Exception {
      //given
      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      //when
      //then
      CommonResponse<CursorPageApiResponse<PostSearchItem>> searchResponse =
          sendPostSearchRequest(accessToken, KEYWORD_NOT_EXIST, 10, status().isOk());

      assertThat(searchResponse.getData().content()).isEmpty();
    }

    /*[Case #4] 공개 계정 게시글 검색 - 팔로우 없이도 검색 가능*/
    @Order(4)
    @DisplayName("4. 팔로우 상태가 아니면서 공개 계정의 사용자가 올린 게시글이 검색되는지 검증")
    @Test
    public void searchPost_shouldIncludePublicUserPost_whenNotFollowing() throws Exception {
      //given
      // 공개 계정 사용자와 게시글 로드
      postFixtureLoader.load("/posts/search/public-account-user.json",
          "/posts/search/public-account-post.json");

      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      //when
      //then
      CommonResponse<CursorPageApiResponse<PostSearchItem>> searchResponse =
          sendPostSearchRequest(accessToken, KEYWORD_PUBLIC, 10, status().isOk());

      assertThat(searchResponse.getData().content()).hasSize(1);
      assertThat(searchResponse.getData().content().getFirst().author().userId()).isEqualTo(
          "public_usr");
    }

    /*[Case #5] 비공개 계정 + 팔로우 - 검색 가능*/
    @Order(5)
    @DisplayName("5. 팔로우 상태이면서 비공개 계정인 사용자가 올린 게시글이 검색되는지 검증")
    @Test
    public void searchPost_shouldIncludePrivateUserPost_whenFollowing() throws Exception {
      //given
      // 비공개 계정 사용자와 게시글 로드
//      postFixtureLoader.load("/posts/search/private-account-user.json",
//          "/posts/search/private-account-post.json");
//
      // usr1 -> private_usr 팔로우 관계 설정
      userFollowFixtureLoader.load(
          "/posts/search/private-account-user.json",
          true,  // 비공개 계정
          "/posts/search/follow-relation.json"
      );
      postFixtureLoader.load("/posts/search/private-account-post.json", 1);

      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      //when
      //then
      CommonResponse<CursorPageApiResponse<PostSearchItem>> searchResponse =
          sendPostSearchRequest(accessToken, KEYWORD_PRIVATE, 10, status().isOk());

      assertThat(searchResponse.getData().content()).hasSize(1);
      assertThat(searchResponse.getData().content().getFirst().author().userId()).isEqualTo(
          "private_usr");
    }

    /*[Case #6] 내가 작성한 게시글 검색*/
    @Order(6)
    @DisplayName("6. 내가 올린 게시글이 검색되는지 검증")
    @Test
    public void searchPost_shouldIncludeMyPost_whenSearchingOwnContent() throws Exception {
      //given
      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      // 테스트용 게시글 작성
      String myPostContent = "나만의고유한게시글내용12345";
      CommonResponse<CreatePostApiResponse> createPostResponse =
          sendPostRequest(
              generateRequestBody(
                  new com.threadly.post.request.CreatePostRequest(myPostContent, List.of())),
              "/api/posts",
              status().isCreated(),
              new com.fasterxml.jackson.core.type.TypeReference<>() {
              },
              java.util.Map.of("Authorization", "Bearer " + accessToken)
          );

      //when
      CommonResponse<CursorPageApiResponse<PostSearchItem>> searchResponse =
          sendPostSearchRequest(accessToken, "나만의고유한", 10, status().isOk());

      //then
      assertThat(searchResponse.getData().content()).hasSize(1);
      assertThat(searchResponse.getData().content().getFirst().content()).isEqualTo(myPostContent);
    }

    /*[Case #7] RECENT 정렬 검증*/
    @Order(7)
    @DisplayName("7. sortType = RECENT인 경우 응답 검증")
    @Test
    public void searchPost_shouldReturnSortedByRecent_whenSortTypeIsRecent() throws Exception {
      //given
      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      //when
      //then
      CommonResponse<CursorPageApiResponse<PostSearchItem>> searchResponse =
          sendPostSearchRequest(accessToken, KEYWORD_MULTI, PostSearchSortType.RECENT, 10,
              status().isOk());

      assertThat(searchResponse.getData().content()).isNotEmpty();

      // 최신순으로 정렬되어 있는지 확인
      List<PostSearchItem> posts = searchResponse.getData().content();
      for (int i = 0; i < posts.size() - 1; i++) {
        assertThat(posts.get(i).postedAt()).isAfterOrEqualTo(posts.get(i + 1).postedAt());
      }
    }

    /*[Case #8] POPULAR 정렬 검증*/
    @Order(8)
    @DisplayName("8. sortType = POPULAR인 경우 응답 검증")
    @Test
    public void searchPost_shouldReturnSortedByPopular_whenSortTypeIsPopular() throws Exception {
      //given
      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      //when
      //then
      CommonResponse<CursorPageApiResponse<PostSearchItem>> searchResponse =
          sendPostSearchRequest(accessToken, KEYWORD_MULTI, PostSearchSortType.POPULAR, 10,
              status().isOk());

      assertThat(searchResponse.getData().content()).isNotEmpty();
    }


    /*[Case #9] 커서 기반 페이징 - 전체 순회*/
    @Order(9)
    @DisplayName("9. 커서 기반 페이징으로 전체 검색 결과를 마지막 페이지까지 순회하며 조회")
    @Test
    public void searchPost_shouldIterateAllPages_usingCursorPagination() throws Exception {
      //given
      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      //when
      //then
      LocalDateTime cursorTimestamp = null;
      String cursorId = null;
      int limit = 5;
      int size = 0;

      while (true) {
        CommonResponse<CursorPageApiResponse<PostSearchItem>> searchResponse =
            sendPostSearchRequest(
                accessToken,
                KEYWORD_MULTI,
                PostSearchSortType.RECENT,
                cursorTimestamp,
                cursorId,
                limit,
                status().isOk()
            );

        size += searchResponse.getData().content().size();

        /*마지막 페이지인 경우*/
        if (searchResponse.getData().nextCursor().cursorTimestamp() == null) {
          break;
        }

        cursorTimestamp = searchResponse.getData().nextCursor().cursorTimestamp();
        cursorId = searchResponse.getData().nextCursor().cursorId();
      }

      assertThat(size).isEqualTo(14);
    }
  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

    /*[Fail Case #1] keyword 없이 검색*/
    @Order(1)
    @DisplayName("1. keyword 없이 검색 요청 시 전체 게시글 반환")
    @Test
    public void searchPost_shouldReturnAllPosts_whenKeywordIsNull() throws Exception {
      //given
      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      //when
      //then
      CommonResponse<CursorPageApiResponse<PostSearchItem>> searchResponse =
          sendPostSearchRequest(accessToken, null, 10, status().isOk());

      // keyword가 없으면 전체 게시글 반환
      assertThat(searchResponse.getData().content()).isNotEmpty();
    }

    /*[Fail Case #2] 비공개 계정 + 팔로우 없음 - 검색 불가*/
    @Order(2)
    @DisplayName("2. 팔로우 상태가 아니면서 비공개 계정의 사용자가 올린 게시글이 검색되지 않는지 검증")
    @Test
    public void searchPost_shouldNotIncludePrivateUserPost_whenNotFollowing() throws Exception {
      //given
      // 비공개 계정 사용자를 비공개로 설정 (팔로우 관계 없음)
      userFixtureLoader.load("/posts/search/private-account-user.json", UserStatus.ACTIVE, true);
      postFixtureLoader.load( "/posts/search/private-account-post.json", 1);

      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      //when
      //then
      CommonResponse<CursorPageApiResponse<PostSearchItem>> searchResponse =
          sendPostSearchRequest(accessToken, KEYWORD_PRIVATE, 10, status().isOk());

      // 비공개 계정이고 팔로우하지 않았으므로 검색 결과 없음
      assertThat(searchResponse.getData().content()).isEmpty();
    }

    /*[Fail Case #3] 유효하지 않은 sortType*/
    @Order(3)
    @DisplayName("3. 유효하지 않은 sortType(RELEVANCE) 사용 시 적절한 에러 반환")
    @Test
    public void searchPost_shouldReturnError_whenSortTypeIsInvalid() throws Exception {
      //given
      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      //when
      //then
      CommonResponse<CursorPageApiResponse<PostSearchItem>> searchResponse =
          sendPostSearchRequest(accessToken, KEYWORD_MULTI, PostSearchSortType.RELEVANCE, 10,
              status().isBadRequest());

      validateFailResponse(searchResponse, ErrorCode.POST_SEARCH_SORT_TYPE_INVALID);
    }
  }
}
