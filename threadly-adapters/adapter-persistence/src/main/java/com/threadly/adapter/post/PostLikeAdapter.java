package com.threadly.adapter.post;

import com.threadly.mapper.post.PostLikeMapper;
import com.threadly.post.like.CreatePostLikePort;
import com.threadly.post.like.FetchPostLikePort;
import com.threadly.posts.PostLike;
import com.threadly.repository.post.PostLikeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PostLikeAdapter implements FetchPostLikePort , CreatePostLikePort {

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
}
