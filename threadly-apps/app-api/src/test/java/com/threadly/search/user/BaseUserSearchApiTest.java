package com.threadly.search.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.BaseApiTest;
import com.threadly.CommonResponse;
import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.port.user.in.search.dto.UserSearchItem;
import org.springframework.test.web.servlet.ResultMatcher;

/**
 * 사용자 검색 API 테스트 Base 클래스
 */
public class BaseUserSearchApiTest extends BaseApiTest {

  /**
   * 사용자 검색 요청 전송
   *
   * @param accessToken
   * @param keyword
   * @param cursorId
   * @param limit
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public CommonResponse<CursorPageApiResponse<UserSearchItem>> sendUserSearchRequest(
      String accessToken,
      String keyword,
      String cursorId,
      int limit,
      ResultMatcher expectedStatus) throws Exception {

    StringBuilder path = new StringBuilder("/api/users/search?");

    if (keyword != null) {
      path.append("keyword=").append(keyword).append("&");
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
   * 사용자 검색 요청 전송 (간단한 버전 - keyword와 limit만 사용)
   *
   * @param accessToken
   * @param keyword
   * @param limit
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public CommonResponse<CursorPageApiResponse<UserSearchItem>> sendUserSearchRequest(
      String accessToken,
      String keyword,
      int limit,
      ResultMatcher expectedStatus) throws Exception {
    return sendUserSearchRequest(accessToken, keyword, null, limit, expectedStatus);
  }
}
