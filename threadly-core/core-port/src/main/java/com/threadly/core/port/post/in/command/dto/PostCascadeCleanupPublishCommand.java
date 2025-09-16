package com.threadly.core.port.post.in.command.dto;

/**
 * 게시글 연관 데이터 삭제 발행 dto
 */
public record PostCascadeCleanupPublishCommand(
    String postId
) {

}
