package com.threadly.post.controller.api;

import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.port.post.in.search.dto.PostSearchItem;
import com.threadly.core.port.post.in.search.dto.PostSearchSortType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "게시글 검색 API", description = "게시글 검색 관련 API")
public interface PostSearchApi {

  @Operation(summary = "게시글 검색", description = "키워드로 게시글을 검색합니다.")
  @GetMapping()
  ResponseEntity<CursorPageApiResponse<PostSearchItem>> search(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @Parameter(description = "검색 키워드") @RequestParam(value = "keyword", required = false) String keyword,
      @RequestParam(value = "sortType", defaultValue = "RECENT") PostSearchSortType sortType,
      @RequestParam(value = "cursor_timestamp", required = false) LocalDateTime cursorTimeStamp,
      @RequestParam(value = "cursor_id", required = false) String cursorId,
      @RequestParam(value = "limit", defaultValue = "10") int limit);

}
