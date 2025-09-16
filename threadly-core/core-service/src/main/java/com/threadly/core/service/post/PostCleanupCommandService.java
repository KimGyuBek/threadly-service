package com.threadly.core.service.post;

import com.threadly.core.domain.image.ImageStatus;
import com.threadly.core.domain.post.PostCommentStatus;
import com.threadly.core.port.post.in.command.PostCleanupCommandUseCase;
import com.threadly.core.port.post.in.command.dto.PostCascadeCleanupPublishCommand;
import com.threadly.core.port.post.out.comment.PostCommentCommandPort;
import com.threadly.core.port.post.out.image.PostImageCommandPort;
import com.threadly.core.port.post.out.like.comment.PostCommentLikerCommandPort;
import com.threadly.core.port.post.out.like.post.PostLikeCommandPort;
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

  private final PostImageCommandPort postImageCommandPort;

  private final PostLikeCommandPort postLikeCommandPort;

  private final PostCommentCommandPort postCommentCommandPort;

  private final PostCommentLikerCommandPort postCommentLikerCommandPort;


  @Transactional
  @Override
  public void cleanupAssociation(PostCascadeCleanupPublishCommand command) {

    /*게시글 이미지 삭제 처리*/
    postImageCommandPort.updateStatus(command.postId(), ImageStatus.DELETED);

    /*게시글 좋아요 삭제 처리*/
    postLikeCommandPort.deleteAllByPostId(command.postId());

    /*게시글 댓글 삭제 처리*/
    postCommentCommandPort.updateAllCommentStatusByPostId(command.postId(),
        PostCommentStatus.DELETED);

    /*게시글 댓글 좋아요 삭제 처리*/
    postCommentLikerCommandPort.deleteAllByPostId(command.postId());

    log.info("Cleanup association complete: postId={}", command.postId());
  }
}
