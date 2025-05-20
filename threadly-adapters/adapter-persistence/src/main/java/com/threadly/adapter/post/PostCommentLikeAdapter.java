package com.threadly.adapter.post;

import com.threadly.mapper.post.CommentLikeMapper;
import com.threadly.post.comment.like.CreatePostCommentLikePort;
import com.threadly.post.comment.like.DeletePostCommentLikePort;
import com.threadly.post.comment.like.FetchPostCommentLikePort;
import com.threadly.posts.comment.CommentLike;
import com.threadly.repository.post.comment.CommentLikeJpaRepository;
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
  public long getLikeCountByCommentId(String commentId) {
    return
        commentLikeJpaRepository.countByCommentId(commentId);
  }

  @Override
  public void deletePostCommentLike(String commentId, String userId) {
    commentLikeJpaRepository.deleteByCommentIdAndUserId(commentId, userId);
  }
}
