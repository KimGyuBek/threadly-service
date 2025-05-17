package com.threadly.post;

import static com.threadly.posts.PostStatusType.*;

import com.threadly.ErrorCode;
import com.threadly.exception.post.PostException;
import com.threadly.exception.user.UserException;
import com.threadly.post.command.CreatePostCommand;
import com.threadly.post.command.DeletePostCommand;
import com.threadly.post.command.UpdatePostCommand;
import com.threadly.post.response.CreatePostApiResponse;
import com.threadly.post.response.PostDetailApiResponse;
import com.threadly.post.response.PostDetailListApiResponse;
import com.threadly.post.response.PostDetailResponse;
import com.threadly.post.response.UpdatePostApiResponse;
import com.threadly.posts.Post;
import com.threadly.posts.PostStatusType;
import com.threadly.user.FetchUserPort;
import com.threadly.user.UserProfile;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시글 관련 Service 구현체
 */
@Service
@RequiredArgsConstructor
public class PostService implements CreatePostUseCase, UpdatePostUseCase, FetchPostUseCase {

  private final CreatePostPort createPostPort;
  private final FetchPostPort fetchPostPort;
  private final UpdatePostPort updatePostPort;

  private final FetchUserPort fetchUserPort;

  @Override
  public CreatePostApiResponse createPost(CreatePostCommand command) {

    /*사용자 프로필 조회*/
    UserProfile userProfile = getUserProfile(command.getUserId());

    /*post 도메인 생성*/
    Post newPost = Post.newPost(command.getUserId(), command.getContent());

    /*post 저장*/
    Post savedPost = createPostPort.savePost(newPost);

    return new CreatePostApiResponse(
        savedPost.getPostId(),
        userProfile.getProfileImageUrl(),
        userProfile.getNickname(),
        savedPost.getUserId(),
        savedPost.getContent(),
        savedPost.getLikesCount(),
        savedPost.getCommentsCount(),
        savedPost.getPostedAt()
    );
  }


  @Transactional
  @Override
  public UpdatePostApiResponse updatePost(UpdatePostCommand command) {
    /*게시글 조회*/
    Post post = fetchPostPort.findById(command.getPostId()).orElseThrow(
        () -> new PostException(ErrorCode.POST_NOT_FOUND)
    );

    /*작성자와 수정 요청 userId가 일치하지 않는 경우*/
    if (!post.getUserId().equals(command.getUserId())) {
      throw new PostException(ErrorCode.POST_UPDATE_FORBIDDEN);
    }

    /*Post updatedPost = 사용자 조회*/
    UserProfile userProfile = getUserProfile(command.getUserId());

    /*게시글 수정*/
    post.updateContent(command.getContent());
    updatePostPort.updatePost(post);

    return new UpdatePostApiResponse(
        post.getPostId(),
        userProfile.getProfileImageUrl(),
        userProfile.getNickname(),
        post.getUserId(),
        post.getContent(),
        post.getLikesCount(),
        post.getCommentsCount(),
        post.getPostedAt()
    );
  }

  @Transactional(readOnly = true)
  @Override
  public PostDetailApiResponse getPost(String postId) {
    PostDetailResponse postDetailResponse = fetchPostPort.findPostDetailsByPostId(postId)
        .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

    PostStatusType status = postDetailResponse.getPostStatus();
    if (status == DELETED) {
      throw new PostException(ErrorCode.POST_ALREADY_DELETED);
    } else if (status == ARCHIVE) {
      throw new PostException(ErrorCode.POST_NOT_FOUND);
    } else if (status == BLOCKED) {
      throw new PostException(ErrorCode.POST_BLOCKED);
    }

    return new PostDetailApiResponse(
        postDetailResponse.getPostId(),
        postDetailResponse.getUserId(),
        postDetailResponse.getUserProfileImageUrl(),
        postDetailResponse.getUserNickname(),
        postDetailResponse.getContent(),
        postDetailResponse.getViewCount(),
        postDetailResponse.getPostedAt()
    );
  }

  @Override
  public PostDetailListApiResponse getUserVisiblePostList() {

    List<PostDetailApiResponse> postList = fetchPostPort.findUserVisiblePostList().stream().map(
        post -> new PostDetailApiResponse(
            post.getPostId(),
            post.getUserId(),
            post.getUserProfileImageUrl(),
            post.getUserNickname(),
            post.getContent(),
            post.getViewCount(),
            post.getPostedAt()
        )
    ).toList();

    if (postList == null || postList.isEmpty()) {
      throw new PostException(ErrorCode.POST_NOT_FOUND);
    }

    return new PostDetailListApiResponse(
        postList
    );
  }

  @Transactional
  @Override
  public void deletePost(DeletePostCommand command) {
    /*게시글 조회*/
    Post post = fetchPostPort.findById(command.getPostId())
        .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

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

    /*삭제 상태 변경 수행*/
    post.markAsDeleted();
    updatePostPort.changeStatus(post);

  }

  /**
   * userProfile 조회
   *
   * @param userId
   * @return
   */
  private UserProfile getUserProfile(String userId) {
    UserProfile userProfile = fetchUserPort.getUserProfile(userId)
        .orElseThrow(() -> new UserException(
            ErrorCode.USER_PROFILE_NOT_FOUND));
    return userProfile;
  }
}
