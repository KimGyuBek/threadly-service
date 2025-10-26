package com.threadly.user.controller;

import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.port.user.in.search.SearchUserQueryUseCase;
import com.threadly.core.port.user.in.search.dto.UserSearchQuery;
import com.threadly.core.port.user.in.search.dto.UserSearchItem;
import com.threadly.user.controller.api.UserSearchApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 검색 Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/search")
public class UserSearchController implements UserSearchApi {

  private final SearchUserQueryUseCase searchUserQueryUseCase;

  /**
   * 주어진 파라미터에 대한 사용자 닉네임 검색
   *
   * @return
   */

  @GetMapping()
  public ResponseEntity<CursorPageApiResponse<UserSearchItem>> search(
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @RequestParam(value = "keyword", required = false) String keyword,
      @RequestParam(value = "cursor_id", required = false) String cursorId,
      @RequestParam(value = "limit", defaultValue = "10") int limit
  ) {

    return ResponseEntity.status(200).body(searchUserQueryUseCase.searchByKeyword(
        new UserSearchQuery(
            user.getUserId(),
            keyword,
            cursorId,
            limit
        ))
    );
  }

}
