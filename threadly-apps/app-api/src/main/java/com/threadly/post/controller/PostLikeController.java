package com.threadly.post.controller;

import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.port.post.in.like.post.command.PostLikeCommandUseCase;
import com.threadly.core.port.post.in.like.post.command.dto.LikePostApiResponse;
import com.threadly.core.port.post.in.like.post.command.dto.LikePostCommand;
import com.threadly.core.port.post.in.like.post.query.GetPostLikeQueryUseCase;
import com.threadly.core.port.post.in.like.post.query.dto.GetPostLikersQuery;
import com.threadly.core.port.post.in.query.PostQueryUseCase;
import com.threadly.core.port.post.in.query.dto.GetPostEngagementApiResponse;
import com.threadly.core.port.post.in.query.dto.GetPostEngagementQuery;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 게시글 좋아요, 취소, 목록, 요약 조회 컨트롤러
 */
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostLikeController {

  private final PostLikeCommandUseCase postLikeCommandUseCase;
  private final GetPostLikeQueryUseCase getPostLikeQueryUseCase;
  private final PostQueryUseCase postQueryUseCase;

  /*
   * 게시글 좋아요 - POST /api/posts/{postId}/likes
   * 게시글 좋아요 취소 - DELETE /api/posts/{postId}/likes
   * 게시글 활동 요약 조회 - GET /api/posts/{postId}/engagement
   * 게시글 활동 좋아요 목록조회 - GET /api/posts/{postId}/engagement/likes
   */

  /**
   * 게시글 좋아요 요약 조회
   *
   * @param postId
   * @return
   */
  @GetMapping("/{postId}/engagement")
  public ResponseEntity<GetPostEngagementApiResponse> getPostEngagement(
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @PathVariable("postId") String postId) {

    return ResponseEntity.status(200).body(postQueryUseCase.getPostEngagement(
        new GetPostEngagementQuery(
            postId, user.getUserId()
        )
    ));
  }


  /**
   * 특정 게시글 좋아요 누른 사용자 커서기반 조회
   *
   * @return
   */
  @GetMapping("/{postId}/engagement/likes")
  public ResponseEntity<CursorPageApiResponse> getPostLikers(
      @RequestParam(value = "cursor_timestamp", required = false) LocalDateTime cursorTimestamp,
      @RequestParam(value = "cursor_id", required = false) String cursorId,
      @RequestParam(value = "limit", defaultValue = "10") int limit,
      @PathVariable("postId") String postId
  ) {

    return ResponseEntity.status(200).body(getPostLikeQueryUseCase.getPostLikers(
        new GetPostLikersQuery(
            postId, cursorTimestamp, cursorId, limit
        )
    ));
  }


  /**
   * 게시글 좋아요
   *
   * @param postId
   * @return
   */
  @PostMapping("/{postId}/likes")
  public ResponseEntity<LikePostApiResponse> likePost(
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @PathVariable("postId") String postId) {

    return ResponseEntity.status(200).body(
        postLikeCommandUseCase.likePost(
            new LikePostCommand(
                postId,
                user.getUserId()
            )
        )
    );
  }

  /**
   * 게시글 좋아요 취소
   *
   * @param postId
   * @return
   */
  @DeleteMapping("/{postId}/likes")
  public ResponseEntity<LikePostApiResponse> cancelPostLike(
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @PathVariable("postId") String postId) {

    return ResponseEntity.status(204).body(postLikeCommandUseCase.cancelLikePost(
        new LikePostCommand(
            postId,
            user.getUserId()
        )
    ));
  }

}
