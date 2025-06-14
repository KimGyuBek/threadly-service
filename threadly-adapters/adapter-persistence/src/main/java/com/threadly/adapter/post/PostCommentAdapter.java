package com.threadly.adapter.post;

import com.threadly.entity.post.PostCommentEntity;
import com.threadly.mapper.post.PostCommentMapper;
import com.threadly.post.comment.create.CreatePostCommentPort;
import com.threadly.post.comment.create.CreatePostCommentResponse;
import com.threadly.post.comment.fetch.FetchPostCommentPort;
import com.threadly.post.comment.fetch.PostCommentDetailForUserProjection;
import com.threadly.post.comment.update.UpdatePostCommentPort;
import com.threadly.post.PostCommentStatusType;
import com.threadly.post.comment.PostComment;
import com.threadly.repository.post.comment.PostCommentJpaRepository;
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
public class PostCommentAdapter implements CreatePostCommentPort, FetchPostCommentPort,
    UpdatePostCommentPort {

  private final PostCommentJpaRepository postCommentJpaRepository;

  @Override
  public CreatePostCommentResponse savePostComment( PostComment postComment) {
    PostCommentEntity postCommentEntity = PostCommentEntity.newComment(postComment);

    postCommentJpaRepository.save(postCommentEntity);

    return new CreatePostCommentResponse(
        postComment.getCommentId(),
        postComment.getUserId(),
        postComment.getContent(),
        LocalDateTime.now()
    );
  }

  @Override
  public Optional<PostComment> fetchById(String commentId) {
    return
        postCommentJpaRepository.findById(commentId).map(PostCommentMapper::toDomain);
  }

  @Override
  public void updatePostCommentStatus(String commentId, PostCommentStatusType status) {
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
  public Optional<PostCommentStatusType> fetchCommentStatus(String commentId) {
    return
        postCommentJpaRepository.findPostCommentStatus(commentId);
  }
}
