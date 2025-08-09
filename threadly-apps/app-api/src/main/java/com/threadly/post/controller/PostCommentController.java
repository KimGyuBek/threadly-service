package com.threadly.post.controller;

import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.post.comment.create.CreatePostCommentApiResponse;
import com.threadly.post.comment.create.CreatePostCommentCommand;
import com.threadly.post.comment.create.CreatePostCommentUseCase;
import com.threadly.post.comment.delete.DeletePostCommentCommand;
import com.threadly.post.comment.delete.DeletePostCommentUseCase;
import com.threadly.post.comment.get.GetPostCommentApiResponse;
import com.threadly.post.comment.get.GetPostCommentListQuery;
import com.threadly.post.comment.get.GetPostCommentUseCase;
import com.threadly.post.request.CreatePostCommentRequest;
import com.threadly.response.CursorPageApiResponse;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 게시글 댓글 생성, 삭제, 조회 컨트롤러
 */
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostCommentController {


  private final CreatePostCommentUseCase createPostCommentUseCase;
  private final DeletePostCommentUseCase deletePostCommentUseCase;
  private final GetPostCommentUseCase getPostCommentUseCase;

  /*
   * 게시글 댓글 생성 - POST /api/posts/{postId}/comments
   * 게시글 댓글 삭제 - DELETE /api/posts/{postId}/comments
   * 게시글 댓글 목록 조회 - GET /api/posts/{postId}/comments
   */


  /**
   * 게시글 댓글 목록 커서 기반 조회
   *
   * @param postId
   * @param cursorTimestamp
   * @param cursorId
   * @param limit
   * @return
   */
  @GetMapping("/{postId}/comments")
  public ResponseEntity<CursorPageApiResponse<GetPostCommentApiResponse>> getPostComments(
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @PathVariable String postId,
      @RequestParam(value = "cursor_timestamp", required = false) LocalDateTime cursorTimestamp ,
      @RequestParam(value = "cursor_id", required = false) String cursorId,
      @RequestParam(value = "limit", defaultValue = "10") int limit
  ) {

    return ResponseEntity.status(200).body(
        getPostCommentUseCase.getPostCommentDetailListForUser(
            new GetPostCommentListQuery(
                postId,
                user.getUserId(),
                cursorTimestamp,
                cursorId,
                limit
            )
        )
    );
  }


  /**
   * 게시글 댓글 생성
   *
   * @param postId
   * @param request
   * @return createPostCommentApiResponse
   */
  @PostMapping("/{postId}/comments")
  public ResponseEntity<CreatePostCommentApiResponse> createPostComment(
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @PathVariable("postId") String postId, @RequestBody @Valid CreatePostCommentRequest request) {

    return ResponseEntity.status(201).body(
        createPostCommentUseCase.createPostComment(
            new CreatePostCommentCommand(
                postId,
                user.getUserId(),
                request.content()
            )
        )
    );
  }

  /**
   * 게시글 댓글 삭제
   *
   * @param commentId
   * @return
   */
  @DeleteMapping("/{postId}/comments/{commentId}")
  public ResponseEntity<Void> deletePostComment(
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @PathVariable("postId") String postId,
      @PathVariable("commentId") String commentId) {

    deletePostCommentUseCase.softDeletePostComment(
        new DeletePostCommentCommand(
            user.getUserId(),
            postId,
            commentId)
    );

    return ResponseEntity.status(204).build();
  }
}
