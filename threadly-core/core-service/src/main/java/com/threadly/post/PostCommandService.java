package com.threadly.post;

import static com.threadly.post.PostStatus.BLOCKED;
import static com.threadly.post.PostStatus.DELETED;

import com.threadly.exception.ErrorCode;
import com.threadly.exception.post.PostException;
import com.threadly.exception.user.UserException;
import com.threadly.post.comment.delete.DeletePostCommentUseCase;
import com.threadly.post.create.CreatePostApiResponse;
import com.threadly.post.create.CreatePostApiResponse.PostImageApiResponse;
import com.threadly.post.create.CreatePostCommand;
import com.threadly.post.create.CreatePostUseCase;
import com.threadly.post.delete.DeletePostCommand;
import com.threadly.post.delete.DeletePostUseCase;
import com.threadly.post.fetch.FetchPostPort;
import com.threadly.post.fetch.PostDetailProjection;
import com.threadly.post.image.fetch.FetchPostImagePort;
import com.threadly.post.image.update.UpdatePostImagePort;
import com.threadly.post.like.post.DeletePostLikePort;
import com.threadly.post.save.SavePostPort;
import com.threadly.post.update.UpdatePostApiResponse;
import com.threadly.post.update.UpdatePostCommand;
import com.threadly.post.update.UpdatePostPort;
import com.threadly.post.update.UpdatePostUseCase;
import com.threadly.post.update.view.IncreaseViewCountUseCase;
import com.threadly.post.view.RecordPostViewPort;
import com.threadly.properties.TtlProperties;
import com.threadly.user.FetchUserPort;
import com.threadly.user.UserProfile;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 게시글 생성 및 수정 관련 서비스
 */
@Service
@RequiredArgsConstructor
public class PostCommandService implements CreatePostUseCase, UpdatePostUseCase, DeletePostUseCase,
    IncreaseViewCountUseCase {

  private final SavePostPort savePostPort;
  private final FetchPostPort fetchPostPort;
  private final UpdatePostPort updatePostPort;

  private final DeletePostLikePort deletePostLikePort;

  private final FetchUserPort fetchUserPort;

  private final RecordPostViewPort recordPostViewPort;

  private final UpdatePostImagePort updatePostImagePort;
  private final FetchPostImagePort fetchPostImagePort;

  private final DeletePostCommentUseCase deletePostCommentUseCase;

  private final TtlProperties ttlProperties;

  @Transactional
  @Override
  public CreatePostApiResponse createPost(CreatePostCommand command) {

    /*사용자 프로필 조회*/
    UserProfile userProfile = getUserProfile(command.getUserId());

    /*post 도메인 생성*/
    Post newPost = Post.newPost(command.getUserId(), command.getContent());

    /*post 저장*/
    Post savedPost = savePostPort.savePost(newPost);

    /*TODO 굳이 재 조회 해야할까?*/
    List<PostImageApiResponse> postImageApiResponse = new ArrayList<>();

    /*이미지가 존재할 경우*/
    if (!command.getImages().isEmpty()) {
      /*게시글 이미지 상태 변경*/
      command.getImages().forEach(it -> {
        updatePostImagePort.finalizeImage(it.getImageId(), savedPost.getPostId(),
            it.getImageOrder());
      });

      /*게시글 이미지 조회*/
      postImageApiResponse = fetchPostImagePort.findAllByPostIdAndStatus(
          savedPost.getPostId(),
          PostImageStatus.CONFIRMED).stream().map(
          projection -> new PostImageApiResponse(
              projection.getImageId(),
              projection.getImageUrl(),
              projection.getImageOrder()
          )
      ).toList();
    }

    return new CreatePostApiResponse(
        savedPost.getPostId(),
        userProfile.getProfileImageUrl(),
        userProfile.getNickname(),
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
    updatePostPort.updatePost(post);

    PostDetailProjection updatePost = fetchPostPort.fetchPostDetailsByPostIdAndUserId(
            command.getPostId(), command.getUserId())
        .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

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
      throw new PostException(ErrorCode.POST_ALREADY_DELETED_ACTION);
    }
    if (post.getStatus() == BLOCKED) {
      throw new PostException(ErrorCode.POST_DELETE_BLOCKED);
    }

    /*게시글 삭제 처리*/
    post.markAsDeleted();
    updatePostPort.changeStatus(post);

    /*게시글 이미지 삭제 처리*/
    updatePostImagePort.updateStatus(post.getPostId(), PostImageStatus.DELETED);

    /*게시글 좋아요 삭제 처리*/
    deletePostLikePort.deleteAllByPostId(post.getPostId());

    /*댓글 및 댓글 좋아요 삭제 처리*/
    deletePostCommentUseCase.deleteAllCommentsAndLikesByPostId(post.getPostId());
  }

  @Transactional
  @Override
  public void increaseViewCount(String postId, String userId) {
    /*Redis에서 사용자의 조회 기록 조회*/
    /*기록이 없을 경우*/
    if (!recordPostViewPort.existsPostView(postId, userId)) {
      /*조회수 업데이트 */
      updatePostPort.increaseViewCount(postId);
    }

    /*기록 저장*/
    recordPostViewPort.recordPostView(postId, userId, ttlProperties.getPostViewSeconds());
  }

  /**
   * 게시글 조회
   *
   * @param command
   * @return
   */
  private Post getPost(String command) {
    return
        fetchPostPort.fetchById(command).orElseThrow(
            () -> new PostException(ErrorCode.POST_NOT_FOUND)
        );
  }

  /**
   * userProfile 조회
   *
   * @param userId
   * @return
   */
  private UserProfile getUserProfile(String userId) {
    return
        fetchUserPort.getUserProfile(userId)
            .orElseThrow(() -> new UserException(
                ErrorCode.USER_PROFILE_NOT_FOUND));
  }
}
