package com.threadly.post.controller.api;

import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.port.post.in.command.dto.CreatePostApiResponse;
import com.threadly.core.port.post.in.command.dto.UpdatePostApiResponse;
import com.threadly.core.port.post.in.query.dto.PostDetails;
import com.threadly.post.request.CreatePostRequest;
import com.threadly.post.request.UpdatePostRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "게시글 API", description = "게시글 CRUD 관련 API")
public interface PostApi {

  @Operation(summary = "게시글 상세 조회", description = "특정 게시글의 상세 정보를 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공",
          content = @Content(schema = @Schema(implementation = PostDetails.class))),
      @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음", content = @Content)
  })
  @GetMapping("/{postId}")
  ResponseEntity<PostDetails> getPost(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @Parameter(description = "게시글 ID", required = true) @PathVariable String postId);

  @Operation(summary = "게시글 목록 조회", description = "피드에 표시될 게시글 목록을 커서 기반 페이지네이션으로 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공",
          content = @Content(schema = @Schema(implementation = CursorPageApiResponse.class)))
  })
  @GetMapping("")
  ResponseEntity<CursorPageApiResponse> getPostList(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @Parameter(description = "커서 타임스탬프") @RequestParam(value = "cursor_timestamp", required = false) LocalDateTime cursorTimestamp,
      @Parameter(description = "커서 ID") @RequestParam(value = "cursor_id", required = false) String cursorId,
      @Parameter(description = "조회할 게시글 수") @RequestParam(value = "limit", defaultValue = "10") int limit);

  @Operation(summary = "게시글 생성", description = "새로운 게시글을 작성합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "게시글 생성 성공",
          content = @Content(schema = @Schema(implementation = CreatePostApiResponse.class)))
  })
  @PostMapping("")
  ResponseEntity<CreatePostApiResponse> createPost(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @RequestBody CreatePostRequest request);

  @Operation(summary = "게시글 수정", description = "기존 게시글을 수정합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "게시글 수정 성공",
          content = @Content(schema = @Schema(implementation = UpdatePostApiResponse.class)))
  })
  @PatchMapping("/{postId}")
  ResponseEntity<UpdatePostApiResponse> updatePost(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @RequestBody UpdatePostRequest request,
      @Parameter(description = "게시글 ID", required = true) @PathVariable("postId") String postId);

  @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "게시글 삭제 성공", content = @Content)
  })
  @DeleteMapping("/{postId}")
  ResponseEntity<Void> deletePost(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @Parameter(description = "게시글 ID", required = true) @PathVariable String postId);

}
