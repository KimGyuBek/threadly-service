package com.threadly.post.comment;

import com.threadly.ErrorCode;
import com.threadly.exception.post.PostCommentException;
import com.threadly.exception.post.PostException;
import com.threadly.post.FetchPostPort;
import com.threadly.post.comment.delete.DeletePostCommentCommand;
import com.threadly.post.comment.delete.DeletePostCommentUseCase;
import com.threadly.posts.PostStatusType;
import com.threadly.posts.comment.CannotDeleteCommentException.AlreadyDeletedException;
import com.threadly.posts.comment.CannotDeleteCommentException.BlockedException;
import com.threadly.posts.comment.CannotDeleteCommentException.ParentPostInactiveException;
import com.threadly.posts.comment.CannotDeleteCommentException.WriteMismatchException;
import com.threadly.posts.comment.PostComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시글 댓글 변경 관련 Service
 */
@Service
@RequiredArgsConstructor
public class DeletePostCommentService implements DeletePostCommentUseCase {

  private final FetchPostCommentPort fetchPostCommentPort;
  private final UpdatePostCommentPort updatePostCommentPort;

  private final FetchPostPort fetchPostPort;

  @Transactional
  @Override
  public void softDeletePostComment(DeletePostCommentCommand command) {
    /*댓글 조회*/
    PostComment postComment = fetchPostCommentPort.findById(command.getCommentId())
        .orElseThrow((() -> new PostCommentException(
            ErrorCode.POST_COMMENT_NOT_FOUND)));

    /*게시글 상태 조회*/
    PostStatusType postStatus = fetchPostPort.findPostStatusByPostId(
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
