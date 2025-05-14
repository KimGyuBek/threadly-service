package com.threadly.controller.post;

import com.threadly.controller.post.request.CreatePostRequest;
import com.threadly.post.CreatePostUseCase;
import com.threadly.post.command.CreatePostCommand;
import com.threadly.post.response.CreatePostApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * post controller
 */
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

  private final CreatePostUseCase createPostUseCase;

  /*
   * 게시글 저장 - POST /api/posts
   * 게시글 수정 - PATCH /api/posts/{postId}
   * 게시글 좋아요 - POST /api/posts/{postId}/likes
   * 게시글 좋아요 목록 조회 - GET /api/posts/{postId}/likes
   * 게시글 댓글 목록 조회 - GET /api/posts/{postId}/comments
   * 게시글 댓글 좋아요 - POST /api/posts/{postId}/comments/{commentId}/likes
   * 게시글 댓글 좋아요 목록 조회 - GET /api/posts/{postId}/comments/{commentsId}/likes
   */

  /**
   * 게시글 생성
   *
   * @return
   */
  @PostMapping("")
  public ResponseEntity<CreatePostApiResponse> createPost(@RequestBody CreatePostRequest request) {
    /*userId 추출*/
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String userId = (String) auth.getCredentials();

    return ResponseEntity.status(201).body(
        createPostUseCase.createPost(
            new CreatePostCommand(userId, request.content())
        )
    );
  }


}
