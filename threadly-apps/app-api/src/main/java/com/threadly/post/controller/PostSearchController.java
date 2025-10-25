package com.threadly.post.controller;

import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.port.post.in.search.SearchPostQueryUseCase;
import com.threadly.core.port.post.in.search.dto.PostSearchSortType;
import com.threadly.core.port.post.in.search.dto.PostSearchItem;
import com.threadly.core.port.post.in.search.dto.PostSearchQuery;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 게시글 검색 Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/search")
public class PostSearchController {

  private final SearchPostQueryUseCase searchPostQueryUsecase;

  /**
   * 주어진 파라미터에 대한 게시글 검색
   *
   * @return
   */

  @GetMapping()
  public ResponseEntity<CursorPageApiResponse<PostSearchItem>> search(
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @RequestParam(value = "keyword", required = false) String keyword,
      @RequestParam(value = "sortType", defaultValue = "RECENT") PostSearchSortType sortType,
      @RequestParam(value = "cursor_timestamp", required = false) LocalDateTime cursorTimeStamp,
      @RequestParam(value = "cursor_id", required = false) String cursorId,
      @RequestParam(value = "limit", defaultValue = "10") int limit
  ) {

    return
        ResponseEntity.status(200).body(searchPostQueryUsecase.searchByKeyword(
            new PostSearchQuery(
                user.getUserId(),
                keyword,
                sortType,
                cursorId,
                cursorTimeStamp,
                limit
            ))
        );
  }

}
