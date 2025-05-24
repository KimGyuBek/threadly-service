package com.threadly.adapter.post;

import com.threadly.mapper.post.PostLikeMapper;
import com.threadly.post.like.CreatePostLikePort;
import com.threadly.post.like.DeletePostLikePort;
import com.threadly.post.like.FetchPostLikePort;
import com.threadly.post.like.projection.PostLikerProjection;
import com.threadly.posts.PostLike;
import com.threadly.repository.post.PostLikeJpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PostLikeAdapter implements FetchPostLikePort, CreatePostLikePort, DeletePostLikePort {

  private final PostLikeJpaRepository postLikeJpaRepository;

  @Override
  public boolean existsByPostIdAndUserId(String postId, String userId) {
    return
        postLikeJpaRepository.existByPostIdAndUserId(postId, userId);
  }

  @Override
  public long getLikeCountByPostId(String postId) {
    return
        postLikeJpaRepository.countByPostId(postId);
  }

  @Override
  public void createPostLike(PostLike postLike) {
    postLikeJpaRepository.save(
        PostLikeMapper.toEntity(postLike)
    );
  }

  @Override
  public int deleteByPostIdAndUserId(String postId, String userId) {
    return postLikeJpaRepository.deleteByPostIdAndUserId(postId, userId);
  }

  @Override
  public List<PostLikerProjection> getPostLikersBeforeCreatedAt(String postId,
      LocalDateTime cursorLikedAt, String cursorLikerId, int limit) {
    return postLikeJpaRepository.getPostLikersBeforeCreatedAt(postId, cursorLikedAt, cursorLikerId,
        limit);
  }
}
