package com.threadly.search.post;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.BaseApiTest;
import com.threadly.CommonResponse;
import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.port.post.in.search.dto.PostSearchSortType;
import com.threadly.core.port.post.in.search.dto.PostSearchItem;
import java.time.LocalDateTime;
import org.springframework.test.web.servlet.ResultMatcher;

/**
 * 게시글 검색 API 테스트 Base 클래스
 */
public class BasePostSearchApiTest extends BaseApiTest {

  /**
   * 게시글 검색 요청 전송
   *
   * @param accessToken
   * @param keyword
   * @param sortType
   * @param cursorTimestamp
   * @param cursorId
   * @param limit
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public CommonResponse<CursorPageApiResponse<PostSearchItem>> sendPostSearchRequest(
      String accessToken,
      String keyword,
      PostSearchSortType sortType,
      LocalDateTime cursorTimestamp,
      String cursorId,
      int limit,
      ResultMatcher expectedStatus) throws Exception {

    StringBuilder path = new StringBuilder("/api/posts/search?");

    if (keyword != null) {
      path.append("keyword=").append(keyword).append("&");
    }
    if (sortType != null) {
      path.append("sortType=").append(sortType.name()).append("&");
    }
    if (cursorTimestamp != null) {
      path.append("cursor_timestamp=").append(cursorTimestamp).append("&");
    }
    if (cursorId != null) {
      path.append("cursor_id=").append(cursorId).append("&");
    }
    path.append("limit=").append(limit);

    return sendGetRequest(
        accessToken,
        path.toString(),
        expectedStatus,
        new TypeReference<>() {
        }
    );
  }

  /**
   * 게시글 검색 요청 전송 (간단한 버전 - keyword와 limit만 사용)
   *
   * @param accessToken
   * @param keyword
   * @param limit
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public CommonResponse<CursorPageApiResponse<PostSearchItem>> sendPostSearchRequest(
      String accessToken,
      String keyword,
      int limit,
      ResultMatcher expectedStatus) throws Exception {
    return sendPostSearchRequest(accessToken, keyword, null, null, null, limit, expectedStatus);
  }

  /**
   * 게시글 검색 요청 전송 (sortType 포함)
   *
   * @param accessToken
   * @param keyword
   * @param sortType
   * @param limit
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public CommonResponse<CursorPageApiResponse<PostSearchItem>> sendPostSearchRequest(
      String accessToken,
      String keyword,
      PostSearchSortType sortType,
      int limit,
      ResultMatcher expectedStatus) throws Exception {
    return sendPostSearchRequest(accessToken, keyword, sortType, null, null, limit,
        expectedStatus);
  }

  /**
   * 게시글 검색 요청 전송 (커서 포함, sortType 없음)
   *
   * @param accessToken
   * @param keyword
   * @param cursorTimestamp
   * @param cursorId
   * @param limit
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public CommonResponse<CursorPageApiResponse<PostSearchItem>> sendPostSearchRequest(
      String accessToken,
      String keyword,
      LocalDateTime cursorTimestamp,
      String cursorId,
      int limit,
      ResultMatcher expectedStatus) throws Exception {
    return sendPostSearchRequest(accessToken, keyword, null, cursorTimestamp, cursorId, limit,
        expectedStatus);
  }
}
