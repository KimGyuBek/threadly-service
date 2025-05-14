package com.threadly.post;

import com.threadly.ErrorCode;
import com.threadly.exception.user.UserException;
import com.threadly.port.CreatePostPort;
import com.threadly.post.command.CreatePostCommand;
import com.threadly.post.response.CreatePostApiResponse;
import com.threadly.posts.Post;
import com.threadly.user.FetchUserPort;
import com.threadly.user.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 게시글 관련 Service 구현체
 */
@Service
@RequiredArgsConstructor
public class PostService implements CreatePostUseCase {

  private final CreatePostPort createPostPort;
  private final FetchUserPort fetchUserPort;

  @Override
  public CreatePostApiResponse createPost(CreatePostCommand createPostCommand) {

    /*사용자 프로필 조회*/
    UserProfile userProfile = fetchUserPort.getUserProfile(createPostCommand.getUserId())
        .orElseThrow(() -> new UserException(
            ErrorCode.USER_PROFILE_NOT_FOUND));

    /*post 도메인 생성*/
    Post newPost = Post.newPost(createPostCommand.getUserId(), createPostCommand.getContent());

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
}
