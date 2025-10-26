package com.threadly.post.controller;

import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.port.post.in.command.PostCommandUseCase;
import com.threadly.core.port.post.in.command.dto.CreatePostCommand;
import com.threadly.core.port.post.in.command.dto.CreatePostCommand.ImageCommand;
import com.threadly.core.port.post.in.command.dto.DeletePostCommand;
import com.threadly.core.port.post.in.command.dto.UpdatePostCommand;
import com.threadly.core.port.post.in.command.dto.CreatePostApiResponse;
import com.threadly.core.port.post.in.query.dto.GetPostListQuery;
import com.threadly.core.port.post.in.query.dto.GetPostQuery;
import com.threadly.core.port.post.in.query.dto.PostDetails;
import com.threadly.core.port.post.in.query.PostQueryUseCase;
import com.threadly.core.port.post.in.command.dto.UpdatePostApiResponse;
import com.threadly.core.port.post.in.view.IncreaseViewCountUseCase;
import com.threadly.post.request.CreatePostRequest;
import com.threadly.post.request.UpdatePostRequest;
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
import com.threadly.post.controller.api.PostApi;


/**
 * 게시글 등록, 조회, 삭제 컨트롤러
 */
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController implements PostApi {

  private final PostCommandUseCase postCommandUseCase;
  private final PostQueryUseCase postQueryUseCase;

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
  public ResponseEntity<PostDetails> getPost(
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @PathVariable String postId) {

    /*게시글 조회*/
    PostDetails body = postQueryUseCase.getPost(
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
  public ResponseEntity<CursorPageApiResponse> getPostList(
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @RequestParam(value = "cursor_timestamp", required = false) LocalDateTime cursorTimestamp,
      @RequestParam(value = "cursor_id", required = false) String cursorId,
      @RequestParam(value = "limit", defaultValue = "10") int limit
  ) {
    return ResponseEntity.status(200).body(
        postQueryUseCase.getUserVisiblePostListByCursor(
            new GetPostListQuery(
                user.getUserId(), cursorTimestamp, cursorId, limit
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
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @Valid @RequestBody CreatePostRequest request) {

    return ResponseEntity.status(201).body(
        postCommandUseCase.createPost(
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
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @Valid @RequestBody UpdatePostRequest request,
      @PathVariable("postId") String postId) {

    return ResponseEntity.status(200).body(
        postCommandUseCase.updatePost(
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
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @PathVariable("postId") String postId) {

    postCommandUseCase.softDeletePost(
        new DeletePostCommand(postId, user.getUserId())
    );

    return ResponseEntity.status(200).build();
  }
}
