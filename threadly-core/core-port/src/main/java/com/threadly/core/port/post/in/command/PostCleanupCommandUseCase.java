package com.threadly.core.port.post.in.command;

import com.threadly.core.port.post.in.command.dto.PostCascadeCleanupPublishCommand;

public interface PostCleanupCommandUseCase {

  /**
   * command.postId에 해당하는 게시글의 연관 데이터 삭제 처리
   *
   * @param command
   */
  void cleanupAssociation(PostCascadeCleanupPublishCommand command);

}
