package com.threadly.post;

import static com.threadly.posts.PostStatusType.BLOCKED;
import static com.threadly.posts.PostStatusType.DELETED;

import com.threadly.ErrorCode;
import com.threadly.exception.post.PostException;
import com.threadly.exception.user.UserException;
import com.threadly.post.create.CreatePostCommand;
import com.threadly.post.delete.DeletePostCommand;
import com.threadly.post.update.UpdatePostCommand;
import com.threadly.post.create.CreatePostUseCase;
import com.threadly.post.create.CreatePostApiResponse;
import com.threadly.post.delete.DeletePostUseCase;
import com.threadly.post.update.UpdatePostApiResponse;
import com.threadly.post.update.UpdatePostUseCase;
import com.threadly.posts.Post;
import com.threadly.user.FetchUserPort;
import com.threadly.user.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 게시글 생성 및 수정 관련 서비스
 */
@Service
@RequiredArgsConstructor
public class PostCommandService implements CreatePostUseCase, UpdatePostUseCase, DeletePostUseCase {

  private final SavePostPort savePostPort;
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
    Post savedPost = savePostPort.savePost(newPost);

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
    Post post = getPost(command.getPostId());

    /*작성자와 수정 요청자의 userId가 일치하지 않는 경우*/
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

    /*삭제 상태 변경 수행*/
    post.markAsDeleted();
    updatePostPort.changeStatus(post);
  }

  /**
   * 게시글 조회
   *
   * @param command
   * @return
   */
  private Post getPost(String command) {
    return
        fetchPostPort.findById(command).orElseThrow(
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
