package com.threadly.user.controller.api;

import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.core.port.user.in.profile.query.dto.GetUserProfileApiResponse;
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
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "사용자 프로필 API", description = "다른 사용자의 프로필 조회 API")
public interface UserProfileApi {

  /**
   * 사용자 프로필 조회
   */
  @Operation(summary = "사용자 프로필 조회", description = "특정 사용자의 공개 프로필 정보를 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "프로필 조회 성공",
          content = @Content(schema = @Schema(implementation = GetUserProfileApiResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
      @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
  })
  @GetMapping("/{userId}")
  ResponseEntity<GetUserProfileApiResponse> getOtherUserProfile(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @Parameter(description = "조회할 사용자 ID", required = true, example = "user-123")
      @PathVariable("userId") String targetUserId);

}
