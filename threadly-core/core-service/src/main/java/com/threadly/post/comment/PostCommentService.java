package com.threadly.post.comment;

import com.threadly.ErrorCode;
import com.threadly.exception.post.PostException;
import com.threadly.exception.user.UserException;
import com.threadly.post.CreatePostCommentUseCase;
import com.threadly.post.FetchPostPort;
import com.threadly.post.command.CreatePostCommentCommand;
import com.threadly.post.comment.response.CreatePostCommentResponse;
import com.threadly.post.response.CreatePostCommentApiResponse;
import com.threadly.posts.Post;
import com.threadly.posts.PostComment;
import com.threadly.posts.PostStatusType;
import com.threadly.user.FetchUserPort;
import com.threadly.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 게시글 댓글 관련 Service
 */
@Service
@RequiredArgsConstructor
public class PostCommentService implements CreatePostCommentUseCase {

  private final FetchPostPort fetchPostPort;

  private final SavePostCommentPort savePostCommentPort;

  private final FetchUserPort fetchUserPort;

  @Override
  public CreatePostCommentApiResponse createPostComment(CreatePostCommentCommand command) {
    /* 게시글 조회*/
    Post post = fetchPostPort.findById(command.getPostId()).orElseThrow(() -> new PostException(
        ErrorCode.POST_NOT_FOUND));

    /*게시글 상태 검증*/
    if (post.getStatus() == PostStatusType.DELETED) {
      throw new PostException(ErrorCode.POST_ALREADY_DELETED);
    } else if (post.getStatus() == PostStatusType.BLOCKED) {
      throw new PostException(ErrorCode.POST_BLOCKED);
    } else if (post.getStatus() == PostStatusType.ARCHIVE) {
      throw new PostException(ErrorCode.POST_ARCHIVED);
    }

    /*사용자 조회*/
    User user = fetchUserPort.findByUserIdWithUserProfile(command.getUserId())
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

    /*게시글 생성*/
    PostComment newComment = post.addComment(command.getUserId(), command.getContent());

    /*저장*/
    /*TODO DTO로 묶기*/
    CreatePostCommentResponse createPostCommentResponse = savePostCommentPort.savePostComment(post,
        newComment, user);

    return new CreatePostCommentApiResponse(
        createPostCommentResponse.commentId(),
        createPostCommentResponse.userId(),
        user.getNickname(),
        user.getProfileImageUrl(),
        createPostCommentResponse.content(),
        createPostCommentResponse.createdAt()
    );
  }
}
