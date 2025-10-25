package com.threadly.adapter.persistence.post.adapter;

import com.threadly.adapter.persistence.post.mapper.PostLikeMapper;
import com.threadly.adapter.persistence.post.repository.PostLikeJpaRepository;
import com.threadly.core.domain.post.PostLike;
import com.threadly.core.port.post.out.like.post.PostLikeCommandPort;
import com.threadly.core.port.post.out.like.post.PostLikeQueryPort;
import com.threadly.core.port.post.out.like.post.PostLikerProjection;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PostLikePersistenceAdapter implements PostLikeQueryPort,
    PostLikeCommandPort {

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
