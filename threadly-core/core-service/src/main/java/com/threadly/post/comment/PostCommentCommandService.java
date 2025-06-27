package com.threadly.post.comment;

import com.threadly.exception.ErrorCode;
import com.threadly.exception.post.PostCommentException;
import com.threadly.exception.post.PostException;
import com.threadly.exception.user.UserException;
import com.threadly.post.PostStatus;
import com.threadly.post.comment.create.CreatePostCommentApiResponse;
import com.threadly.post.comment.create.CreatePostCommentCommand;
import com.threadly.post.comment.create.CreatePostCommentPort;
import com.threadly.post.comment.create.CreatePostCommentResponse;
import com.threadly.post.comment.create.CreatePostCommentUseCase;
import com.threadly.post.comment.delete.DeletePostCommentCommand;
import com.threadly.post.comment.delete.DeletePostCommentUseCase;
import com.threadly.post.comment.fetch.FetchPostCommentPort;
import com.threadly.post.comment.update.UpdatePostCommentPort;
import com.threadly.post.fetch.FetchPostPort;
import com.threadly.post.Post;
import com.threadly.post.comment.CannotDeleteCommentException.AlreadyDeletedException;
import com.threadly.post.comment.CannotDeleteCommentException.BlockedException;
import com.threadly.post.comment.CannotDeleteCommentException.ParentPostInactiveException;
import com.threadly.post.comment.CannotDeleteCommentException.WriteMismatchException;
import com.threadly.user.FetchUserPort;
import com.threadly.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시글 댓글 관련 Service
 */
@Service
@RequiredArgsConstructor
public class PostCommentCommandService implements CreatePostCommentUseCase,
    DeletePostCommentUseCase {

  private final FetchPostPort fetchPostPort;

  private final CreatePostCommentPort createPostCommentPort;
  private final FetchPostCommentPort fetchPostCommentPort;
  private final UpdatePostCommentPort updatePostCommentPort;

  private final FetchUserPort fetchUserPort;

  @Override
  public CreatePostCommentApiResponse createPostComment(CreatePostCommentCommand command) {
    /* 게시글 조회*/
    Post post = fetchPostPort.fetchById(command.getPostId()).orElseThrow(() -> new PostException(
        ErrorCode.POST_NOT_FOUND));

    /*게시글 상태 검증*/
    if (post.getStatus() == PostStatus.DELETED) {
      throw new PostException(ErrorCode.POST_ALREADY_DELETED);
    } else if (post.getStatus() == PostStatus.BLOCKED) {
      throw new PostException(ErrorCode.POST_BLOCKED);
    } else if (post.getStatus() == PostStatus.ARCHIVE) {
      throw new PostException(ErrorCode.POST_ARCHIVED);
    }

    /*사용자 조회*/
    User user = fetchUserPort.findByUserIdWithUserProfile(command.getUserId())
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

    /*게시글 생성*/
    PostComment newComment = post.addComment(command.getUserId(), command.getContent());

    /*저장*/
    /*TODO DTO로 묶기*/
    CreatePostCommentResponse createPostCommentResponse = createPostCommentPort.savePostComment(
        newComment);

    return new CreatePostCommentApiResponse(
        createPostCommentResponse.commentId(),
        createPostCommentResponse.userId(),
        user.getNickname(),
        user.getProfileImageUrl(),
        createPostCommentResponse.content(),
        createPostCommentResponse.createdAt()
    );
  }

  @Transactional
  @Override
  public void softDeletePostComment(DeletePostCommentCommand command) {
    /*댓글 조회*/
    PostComment postComment = fetchPostCommentPort.fetchById(command.getCommentId())
        .orElseThrow((() -> new PostCommentException(
            ErrorCode.POST_COMMENT_NOT_FOUND)));

    /*게시글 상태 조회*/
    PostStatus postStatus = fetchPostPort.fetchPostStatusByPostId(
        command.getPostId()).orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

    /*게시글 댓글 삭제 가능한지 검증*/
    /*TODO Exception Mapper 구현해서 코드 간소화 고려 */
    try {
      postComment.validateDeletableBy(command.getUserId(), postStatus);
    } catch (WriteMismatchException e) {
      throw new PostCommentException(ErrorCode.POST_COMMENT_DELETE_FORBIDDEN);
    } catch (AlreadyDeletedException e) {
      throw new PostCommentException(ErrorCode.POST_COMMENT_ALREADY_DELETED);
    } catch (BlockedException e) {
      throw new PostCommentException(ErrorCode.POST_COMMENT_DELETE_BLOCKED);
    } catch (ParentPostInactiveException e) {
      throw new PostCommentException(ErrorCode.POST_COMMENT_PARENT_POST_INACTIVE);
    }

    /*삭제 상태로 변경*/
    postComment.markAsDeleted();
    updatePostCommentPort.updatePostCommentStatus(postComment.getCommentId(),
        postComment.getStatus());
  }
}
