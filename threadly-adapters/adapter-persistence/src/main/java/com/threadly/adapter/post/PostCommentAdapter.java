package com.threadly.adapter.post;

import com.threadly.entity.post.PostCommentEntity;
import com.threadly.mapper.post.PostCommentMapper;
import com.threadly.post.comment.CreatePostCommentPort;
import com.threadly.post.comment.FetchPostCommentPort;
import com.threadly.post.comment.UpdatePostCommentPort;
import com.threadly.post.comment.response.CreatePostCommentResponse;
import com.threadly.posts.Post;
import com.threadly.posts.PostCommentStatusType;
import com.threadly.posts.comment.PostComment;
import com.threadly.repository.post.comment.PostCommentJpaRepository;
import com.threadly.user.User;
import java.time.LocalDateTime;
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
  public CreatePostCommentResponse savePostComment(Post post, PostComment postComment, User user) {
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
  public Optional<PostComment> findById(String commentId) {
    return
        postCommentJpaRepository.findById(commentId).map(PostCommentMapper::toDomain);
  }

  @Override
  public void updatePostCommentStatus(String commentId, PostCommentStatusType status) {
    postCommentJpaRepository.updatePostCommentStatus(commentId, status);
  }
}
