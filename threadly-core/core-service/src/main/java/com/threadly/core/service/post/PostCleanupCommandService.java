package com.threadly.core.service.post;

import com.threadly.core.domain.image.ImageStatus;
import com.threadly.core.domain.post.PostCommentStatus;
import com.threadly.core.port.post.in.command.PostCleanupCommandUseCase;
import com.threadly.core.port.post.in.command.dto.PostCascadeCleanupPublishCommand;
import com.threadly.core.port.post.out.comment.update.UpdatePostCommentPort;
import com.threadly.core.port.post.out.image.update.UpdatePostImagePort;
import com.threadly.core.port.post.out.like.comment.DeletePostCommentLikePort;
import com.threadly.core.port.post.out.like.post.DeletePostLikePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시글 cleanup 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostCleanupCommandService implements PostCleanupCommandUseCase {

  private final UpdatePostImagePort updatePostImagePort;

  private final DeletePostLikePort deletePostLikePort;

  private final UpdatePostCommentPort updatePostCommentPort;

  private final DeletePostCommentLikePort deletePostCommentLikePort;


  @Transactional
  @Override
  public void cleanupAssociation(PostCascadeCleanupPublishCommand command) {

    /*게시글 이미지 삭제 처리*/
    updatePostImagePort.updateStatus(command.postId(), ImageStatus.DELETED);

    /*게시글 좋아요 삭제 처리*/
    deletePostLikePort.deleteAllByPostId(command.postId());

    /*게시글 댓글 삭제 처리*/
    updatePostCommentPort.updateAllCommentStatusByPostId(command.postId(),
        PostCommentStatus.DELETED);

    /*게시글 댓글 좋아요 삭제 처리*/
    deletePostCommentLikePort.deleteAllByPostId(command.postId());

    log.info("Cleanup association complete: postId={}", command.postId());
  }
}
