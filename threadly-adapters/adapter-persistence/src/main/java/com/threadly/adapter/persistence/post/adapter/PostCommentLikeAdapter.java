package com.threadly.adapter.persistence.post.adapter;

import com.threadly.adapter.persistence.post.mapper.CommentLikeMapper;
import com.threadly.core.port.post.like.comment.CreatePostCommentLikePort;
import com.threadly.core.port.post.like.comment.DeletePostCommentLikePort;
import com.threadly.core.port.post.like.comment.FetchPostCommentLikePort;
import com.threadly.core.port.post.like.comment.PostCommentLikerProjection;
import com.threadly.core.domain.post.comment.CommentLike;
import com.threadly.adapter.persistence.post.repository.CommentLikeJpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 댓글 좋아요 관련 Adapter
 */
@Repository
@RequiredArgsConstructor
public class PostCommentLikeAdapter implements FetchPostCommentLikePort, CreatePostCommentLikePort,
    DeletePostCommentLikePort {

  private final CommentLikeJpaRepository commentLikeJpaRepository;

  @Override
  public boolean existsByCommentIdAndUserId(String commentId, String userId) {
    return
        commentLikeJpaRepository.existsByCommentIdAndUserId(userId, commentId);
  }

  @Override
  public void createPostCommentLike(CommentLike commentLike) {
    commentLikeJpaRepository.save(CommentLikeMapper.toEntity(commentLike));
  }

  @Override
  public long fetchLikeCountByCommentId(String commentId) {
    return
        commentLikeJpaRepository.countByCommentId(commentId);
  }

  @Override
  public void deletePostCommentLike(String commentId, String userId) {
    commentLikeJpaRepository.deleteByCommentIdAndUserId(commentId, userId);
  }

  @Override
  public List<PostCommentLikerProjection> fetchCommentLikerListByCommentIdWithCursor(
      String commentId, LocalDateTime cursorLikedAt, String likerId, int limit) {
    return commentLikeJpaRepository.findPostLikersByCommentIdWithCursor(
        commentId, cursorLikedAt, likerId, limit
    );
  }

  @Override
  public void deleteAllByPostId(String postId) {
    commentLikeJpaRepository.deleteAllByPostId(postId);
  }
}
