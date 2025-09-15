package com.threadly.core.service.post;

import com.threadly.core.port.post.in.command.PostCleanupCommandUseCase;
import com.threadly.core.port.post.in.command.dto.PostCascadeCleanupPublishCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 게시글 연관 데이터 삭제 listener
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PostCascadeCleanupListener {

  private final PostCleanupCommandUseCase postCleanupCommandUseCase;

  @Async("houseKeepingExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onPostDeleted(PostCascadeCleanupPublishCommand command) {

    try {
      postCleanupCommandUseCase.cleanupAssociation(command);
      log.debug("Published postCascadeCleanup event for command: postId={}", command.postId());
    } catch (Exception e) {
      log.error("Failed to cleanup association for post {}", command.postId(), e);
    }
  }
}
