package com.threadly.adapter.persistence.post.adapter;

import com.threadly.adapter.persistence.post.entity.PostCommentEntity;
import com.threadly.adapter.persistence.post.mapper.PostCommentMapper;
import com.threadly.core.domain.post.PostCommentStatus;
import com.threadly.core.domain.post.comment.PostComment;
import com.threadly.core.port.post.comment.create.CreatePostCommentPort;
import com.threadly.core.port.post.comment.fetch.FetchPostCommentPort;
import com.threadly.core.port.post.comment.fetch.PostCommentDetailForUserProjection;
import com.threadly.core.port.post.comment.update.UpdatePostCommentPort;
import com.threadly.adapter.persistence.post.repository.PostCommentJpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 게시글 댓글 관련 Adapter
 */
@Repository
@RequiredArgsConstructor
public class PostCommentPersistenceAdapter implements CreatePostCommentPort, FetchPostCommentPort,
    UpdatePostCommentPort {

  private final PostCommentJpaRepository postCommentJpaRepository;

  @Override
  public void savePostComment(PostComment postComment) {
    postCommentJpaRepository.save(PostCommentEntity.newComment(postComment));
  }

  @Override
  public Optional<PostComment> fetchById(String commentId) {
    return
        postCommentJpaRepository.findById(commentId).map(PostCommentMapper::toDomain);
  }

  @Override
  public void updatePostCommentStatus(String commentId, PostCommentStatus status) {
    postCommentJpaRepository.updatePostCommentStatus(commentId, status);
  }

  @Override
  public Optional<PostCommentDetailForUserProjection> fetchCommentDetail(String commentId,
      String userId) {
    return
        postCommentJpaRepository.findPostCommentDetailForUserByPostId(commentId, userId);
  }

  @Override
  public List<PostCommentDetailForUserProjection> fetchCommentListByPostIdWithCursor(String postId,
      String userId,
      LocalDateTime cursorCommentedAt, String cursorCommenterId, int limit) {
    return postCommentJpaRepository.findPostCommentListForUserByPostId(
        postId, userId, cursorCommentedAt, cursorCommenterId, limit
    );
  }

  @Override
  public Optional<PostCommentStatus> fetchCommentStatus(String commentId) {
    return
        postCommentJpaRepository.findPostCommentStatus(commentId);
  }

  @Override
  public void updateAllCommentStatusByPostId(String postId, PostCommentStatus status) {
    postCommentJpaRepository.updateCommentStatusByPostId(postId, status);

  }
}
