package com.threadly.post.controller;

import com.threadly.auth.AuthenticationUser;
import com.threadly.post.create.CreatePostApiResponse;
import com.threadly.post.create.CreatePostCommand;
import com.threadly.post.create.CreatePostCommand.ImageCommand;
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
import com.threadly.post.update.view.IncreaseViewCountUseCase;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

  private final IncreaseViewCountUseCase increaseViewCountUseCase;

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
  public ResponseEntity<GetPostDetailApiResponse> getPost(
      @AuthenticationPrincipal AuthenticationUser user,
      @PathVariable String postId) {

    /*게시글 조회*/
    GetPostDetailApiResponse body = getPostUseCase.getPost(
        new GetPostQuery(
            postId, user.getUserId()
        )
    );

    /*조회수 증가 처리 */
    increaseViewCountUseCase.increaseViewCount(postId, user.getUserId());

    return ResponseEntity.status(200).body(body);
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
      @AuthenticationPrincipal AuthenticationUser user,
      @RequestParam(value = "cursor_posted_at", required = false) LocalDateTime cursorPostedAt,
      @RequestParam(value = "cursor_post_id", required = false) String cursorPostId,
      @RequestParam(value = "limit", defaultValue = "10") int limit
  ) {
    return ResponseEntity.status(200).body(
        getPostUseCase.getUserVisiblePostListByCursor(
            new GetPostListQuery(
                user.getUserId(), cursorPostedAt, cursorPostId, limit
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
      @AuthenticationPrincipal AuthenticationUser user,
      @Valid @RequestBody CreatePostRequest request) {

    return ResponseEntity.status(201).body(
        createPostUseCase.createPost(
            new CreatePostCommand(
                user.getUserId(),
                request.content(),
                request.images().stream().map(
                    imageRequest -> new ImageCommand(
                        imageRequest.imageId(),
                        imageRequest.imageOrder()
                    )
                ).toList()))
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
      @AuthenticationPrincipal AuthenticationUser user,
      @Valid @RequestBody UpdatePostRequest request,
      @PathVariable("postId") String postId) {

    return ResponseEntity.status(200).body(
        updatePostUseCase.updatePost(
            new UpdatePostCommand(postId, user.getUserId(), request.content())
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
  public ResponseEntity<Void> deletePost(
      @AuthenticationPrincipal AuthenticationUser user,
      @PathVariable("postId") String postId) {

    deletePostUseCase.softDeletePost(
        new DeletePostCommand(postId, user.getUserId())
    );

    return ResponseEntity.status(200).build();
  }
}
