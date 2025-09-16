package com.threadly.core.service.post;

import static com.threadly.core.domain.post.PostStatus.BLOCKED;
import static com.threadly.core.domain.post.PostStatus.DELETED;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.post.PostException;
import com.threadly.commons.properties.TtlProperties;
import com.threadly.core.domain.image.ImageStatus;
import com.threadly.core.domain.post.Post;
import com.threadly.core.port.post.in.command.PostCommandUseCase;
import com.threadly.core.port.post.in.command.dto.CreatePostApiResponse;
import com.threadly.core.port.post.in.command.dto.CreatePostApiResponse.PostImageApiResponse;
import com.threadly.core.port.post.in.command.dto.CreatePostCommand;
import com.threadly.core.port.post.in.command.dto.DeletePostCommand;
import com.threadly.core.port.post.in.command.dto.PostCascadeCleanupPublishCommand;
import com.threadly.core.port.post.in.command.dto.UpdatePostApiResponse;
import com.threadly.core.port.post.in.command.dto.UpdatePostCommand;
import com.threadly.core.port.post.in.view.IncreaseViewCountUseCase;
import com.threadly.core.port.post.out.PostCommandPort;
import com.threadly.core.port.post.out.image.PostImageQueryPort;
import com.threadly.core.port.post.out.image.PostImageCommandPort;
import com.threadly.core.port.post.out.PostQueryPort;
import com.threadly.core.port.post.out.projection.PostDetailProjection;
import com.threadly.core.port.post.out.view.RecordPostViewPort;
import com.threadly.core.port.user.out.profile.query.UserPreviewProjection;
import com.threadly.core.port.user.out.profile.query.UserProfileQueryPort;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 게시글 생성 및 수정 관련 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PostCommandService implements PostCommandUseCase,
    IncreaseViewCountUseCase {

  private final PostCommandPort postCommandPort;
  private final PostQueryPort postQueryPort;


  private final RecordPostViewPort recordPostViewPort;

  private final PostImageCommandPort postImageCommandPort;
  private final PostImageQueryPort postImageQueryPort;

  private final TtlProperties ttlProperties;

  private final UserProfileQueryPort userProfileQueryPort;

  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  @Override
  public CreatePostApiResponse createPost(CreatePostCommand command) {
    /*post 도메인 생성*/
    Post newPost = Post.newPost(command.getUserId(), command.getContent());

    /*post 저장*/
    Post savedPost = postCommandPort.savePost(newPost);

    /*TODO 굳이 재 조회 해야할까?*/
    List<PostImageApiResponse> postImageApiResponse = new ArrayList<>();

    /*이미지가 존재할 경우*/
    if (!command.getImages().isEmpty()) {
      /*게시글 이미지 상태 변경*/
      command.getImages().forEach(it -> {
        postImageCommandPort.finalizeImage(it.getImageId(), savedPost.getPostId(),
            it.getImageOrder());
      });

      /*게시글 이미지 조회*/
      postImageApiResponse = postImageQueryPort.findAllByPostIdAndStatus(
          savedPost.getPostId(),
          ImageStatus.CONFIRMED).stream().map(
          projection -> new PostImageApiResponse(
              projection.getImageId(),
              projection.getImageUrl(),
              projection.getImageOrder()
          )
      ).toList();
    }

    log.info("새 게시글 생성 완료: postId={}", savedPost.getPostId());

    /*사용자 프로필 조회*/
    UserPreviewProjection userPreview = userProfileQueryPort.findUserPreviewByUserId(
        command.getUserId());

    return new CreatePostApiResponse(
        savedPost.getPostId(),
        userPreview.getProfileImageUrl(),
        userPreview.getNickname(),
        savedPost.getUserId(),
        savedPost.getContent(),
        postImageApiResponse,
        savedPost.getPostedAt()
    );
  }

  @Transactional
  @Override
  public UpdatePostApiResponse updatePost(UpdatePostCommand command) {
    /*게시글 조회*/
    Post post = getPost(command.getPostId());

    /*작성자와 수정 요청자의 userId가 일치하지 않는 경우*/
    if (!post.getUserId().equals(command.getUserId())) {
      throw new PostException(ErrorCode.POST_UPDATE_FORBIDDEN);
    }

    /*게시글 수정*/
    post.updateContent(command.getContent());
    postCommandPort.updatePost(post);

    PostDetailProjection updatePost = postQueryPort.fetchPostDetailsByPostIdAndUserId(
            command.getPostId(), command.getUserId())
        .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

    log.info("게시글 업데이트 완료: postId={}", updatePost.getPostId());

    return new UpdatePostApiResponse(
        updatePost.getPostId(),
        updatePost.getUserId(),
        updatePost.getUserProfileImageUrl(),
        updatePost.getUserNickname(),
        updatePost.getContent(),
        updatePost.getViewCount(),
        updatePost.getPostedAt(),
        updatePost.getLikeCount(),
        updatePost.getCommentCount(),
        updatePost.isLiked()
    );
  }

  @Transactional
  @Override
  public void softDeletePost(DeletePostCommand command) {
    /*게시글 조회*/
    Post post = getPost(command.getPostId());

    /*게시글 작성자와 사용자 검증*/
    if (!post.getUserId().equals(command.getUserId())) {
      throw new PostException(ErrorCode.POST_DELETE_FORBIDDEN);
    }

    /*게시글 상태 검증*/
    if (post.getStatus() == DELETED) {
      log.warn("이미 삭제처리된 게시글임: postId={}", post.getPostId());
      throw new PostException(ErrorCode.POST_ALREADY_DELETED_ACTION);
    }
    if (post.getStatus() == BLOCKED) {
      log.warn("이미 BLOCKED된 게시글임: postId={}", post.getPostId());
      throw new PostException(ErrorCode.POST_DELETE_BLOCKED);
    }

    /*게시글 삭제 처리*/
    post.markAsDeleted();
    postCommandPort.changeStatus(post);
    log.info("게시글 삭제 처리 완료: postId={}", post.getPostId());

    /*연관 데이터 삭제 처리*/
    eventPublisher.publishEvent(new PostCascadeCleanupPublishCommand(post.getPostId()));
  }

  @Transactional
  @Override
  public void increaseViewCount(String postId, String userId) {
    /*Redis에서 사용자의 조회 기록 조회*/
    /*기록이 없을 경우*/
    if (!recordPostViewPort.existsPostView(postId, userId)) {
      /*조회수 업데이트 */
      postCommandPort.increaseViewCount(postId);
    }

    /*기록 저장*/
    recordPostViewPort.recordPostView(postId, userId, ttlProperties.getPostViewSeconds());
    log.debug("조회 수 증가 완료: postId={}", postId);
  }


  /**
   * 게시글 조회
   *
   * @param command
   * @return
   */
  private Post getPost(String command) {
    return
        postQueryPort.fetchById(command).orElseThrow(
            () -> new PostException(ErrorCode.POST_NOT_FOUND)
        );
  }

}
