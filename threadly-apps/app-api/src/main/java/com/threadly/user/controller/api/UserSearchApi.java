package com.threadly.user.controller.api;

import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.port.user.in.search.dto.UserSearchItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "사용자 검색 API", description = "사용자 검색 관련 API")
public interface UserSearchApi {

  /**
   * 사용자 닉네임 검색
   */
  @Operation(summary = "사용자 검색", description = "키워드로 사용자를 검색합니다. 커서 기반 페이지네이션을 지원합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "검색 성공",
          content = @Content(schema = @Schema(implementation = CursorPageApiResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
  })
  @GetMapping()
  ResponseEntity<CursorPageApiResponse<UserSearchItem>> search(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @Parameter(description = "검색 키워드 (닉네임)", example = "john")
      @RequestParam(value = "keyword", required = false) String keyword,
      @Parameter(description = "커서 ID (이전 페이지의 마지막 항목 ID)", example = "user-123")
      @RequestParam(value = "cursor_id", required = false) String cursorId,
      @Parameter(description = "한 페이지당 조회할 사용자 수", example = "10")
      @RequestParam(value = "limit", defaultValue = "10") int limit);

}
