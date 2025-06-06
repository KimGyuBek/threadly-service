package com.threadly.post.controller;

import com.threadly.post.create.CreatePostApiResponse;
import com.threadly.post.create.CreatePostCommand;
import com.threadly.post.create.CreatePostUseCase;
import com.threadly.post.delete.DeletePostCommand;
import com.threadly.post.delete.DeletePostUseCase;
import com.threadly.post.get.GetPostDetailApiResponse;
import com.threadly.post.get.GetPostDetailListApiResponse;
import com.threadly.post.get.GetPostListQuery;
import com.threadly.post.get.GetPostQuery;
import com.threadly.post.get.GetPostUseCase;
import com.threadly.post.request.CreatePostRequest;
import com.threadly.post.request.UpdatePostRequest;
import com.threadly.post.update.UpdatePostApiResponse;
import com.threadly.post.update.UpdatePostCommand;
import com.threadly.post.update.UpdatePostUseCase;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 게시글 등록, 조회, 삭제 컨트롤러
 */
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

  private final CreatePostUseCase createPostUseCase;
  private final UpdatePostUseCase updatePostUseCase;
  private final GetPostUseCase getPostUseCase;
  private final DeletePostUseCase deletePostUseCase;

  /*
   * 게시글 저장 - POST /api/posts
   * 게시글 삭제 - DELETE /api/posts/{postId}
   * 게시글 조회 - GET /api/posts/{postId}
   * 게시글 목록 조회 - GET /api/posts
   * 게시글 수정 - PATCH /api/posts/{postId}
   */

  /**
   * postId로 게시글 조회
   *
   * @param postId
   * @return
   */
  @GetMapping("/{postId}")
  public ResponseEntity<GetPostDetailApiResponse> getPost(@PathVariable String postId) {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String userId = (String) auth.getCredentials();

    return ResponseEntity.status(200).body(getPostUseCase.getPost(
        new GetPostQuery(postId, userId)
    ));
  }


  /**
   * 사용자에게 노출되는 게시글 목록을 커서 기반으로 조회
   * <p>
   * 최신 게시글 부터 수정일(postedAt) 기준으로 내림차순 정렬되며, 커서 값보다 이전에 수정된 게시글들을 조회
   *
   * @return
   */
  @GetMapping("")
  public ResponseEntity<GetPostDetailListApiResponse> getPostList(
      @RequestParam(value = "cursor_posted_at", required = false) LocalDateTime cursorPostedAt,
      @RequestParam(value = "cursor_post_id", required = false) String cursorPostId,
      @RequestParam(value = "limit", defaultValue = "10") int limit
  ) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String userId = (String) auth.getCredentials();

    return ResponseEntity.status(200).body(
        getPostUseCase.getUserVisiblePostListByCursor(
            new GetPostListQuery(
                userId, cursorPostedAt, cursorPostId, limit
            )
        )
    );
  }


  /**
   * 게시글 생성
   *
   * @return
   */
  @PostMapping("")
  public ResponseEntity<CreatePostApiResponse> createPost(
      @Valid @RequestBody CreatePostRequest request) {
    /*userId 추출*/
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String userId = (String) auth.getCredentials();

    return ResponseEntity.status(201).body(
        createPostUseCase.createPost(
            new CreatePostCommand(userId, request.content())
        )
    );
  }

  /**
   * 게시글 수정
   *
   * @param request
   * @param postId
   * @return
   */
  @PatchMapping("/{postId}")
  public ResponseEntity<UpdatePostApiResponse> updatePost(
      @Valid @RequestBody UpdatePostRequest request,
      @PathVariable("postId") String postId) {

    /*userId 추출*/
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String userId = (String) auth.getCredentials();

    return ResponseEntity.status(200).body(
        updatePostUseCase.updatePost(
            new UpdatePostCommand(postId, userId, request.content())
        )
    );
  }

  /**
   * 게시글 삭제 상태로 변경
   *
   * @param postId
   * @return
   */
  @DeleteMapping("/{postId}")
  public ResponseEntity<Void> deletePost(@PathVariable("postId") String postId) {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String userId = (String) auth.getCredentials();

    deletePostUseCase.softDeletePost(
        new DeletePostCommand(postId, userId)
    );

    return ResponseEntity.status(200).build();
  }
}
