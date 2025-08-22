package com.threadly.adapter.persistence.post.adapter;

import com.threadly.adapter.persistence.post.mapper.PostLikeMapper;
import com.threadly.core.port.post.like.post.CreatePostLikePort;
import com.threadly.core.port.post.like.post.DeletePostLikePort;
import com.threadly.core.port.post.like.post.FetchPostLikePort;
import com.threadly.core.port.post.like.post.PostLikerProjection;
import com.threadly.core.domain.post.PostLike;
import com.threadly.adapter.persistence.post.repository.PostLikeJpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PostLikePersistenceAdapter implements FetchPostLikePort, CreatePostLikePort, DeletePostLikePort {

  private final PostLikeJpaRepository postLikeJpaRepository;

  @Override
  public boolean existsByPostIdAndUserId(String postId, String userId) {
    return
        postLikeJpaRepository.existByPostIdAndUserId(postId, userId);
  }

  @Override
  public long fetchLikeCountByPostId(String postId) {
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
  public List<PostLikerProjection> fetchPostLikersBeforeCreatedAt(String postId,
      LocalDateTime cursorLikedAt, String cursorLikerId, int limit) {
    return postLikeJpaRepository.getPostLikersBeforeCreatedAt(postId, cursorLikedAt, cursorLikerId,
        limit);
  }

  @Override
  public void deleteAllByPostId(String postId) {
    postLikeJpaRepository.deleteAllByPostId(postId);
  }
}
