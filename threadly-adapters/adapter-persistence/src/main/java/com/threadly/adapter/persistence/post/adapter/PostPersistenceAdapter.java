package com.threadly.adapter.persistence.post.adapter;

import com.threadly.adapter.persistence.post.entity.PostEntity;
import com.threadly.adapter.persistence.post.mapper.PostMapper;
import com.threadly.core.domain.post.PostStatus;
import com.threadly.core.port.post.out.fetch.FetchPostPort;
import com.threadly.core.port.post.out.fetch.PostDetailProjection;
import com.threadly.core.port.post.out.fetch.PostEngagementProjection;
import com.threadly.core.port.post.out.save.SavePostPort;
import com.threadly.core.port.post.out.update.UpdatePostPort;
import com.threadly.core.domain.post.Post;
import com.threadly.adapter.persistence.post.repository.PostJpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 게시글 관련 adapter
 */
@Repository
@RequiredArgsConstructor
public class PostPersistenceAdapter implements SavePostPort, FetchPostPort, UpdatePostPort {

  private final PostJpaRepository postJpaRepository;


  @Override
  public Post savePost(Post post) {
    /*게시글 조회*/
    PostEntity saved = postJpaRepository.save(
        PostMapper.toEntity(post)
    );

    return PostMapper.toDomain(saved);
  }

  @Override
  public Optional<Post> fetchById(String postId) {
    return
        postJpaRepository.findById(postId).map(
            PostMapper::toDomain
        );
  }

  @Override
  public void updatePost(Post post) {
    postJpaRepository.updatePostContentByPostId(post.getPostId(), post.getContent());
  }

  @Override
  public Optional<PostDetailProjection> fetchPostDetailsByPostIdAndUserId(String postId,
      String userId) {
    return
        postJpaRepository.getPostDetailsByPostIdAndUserId(postId, userId);
  }


  @Override
  public List<PostDetailProjection> fetchUserVisiblePostListByCursor(String userId,
      LocalDateTime cursorPostedAt, String cursorPostId,
      int limit) {
    return postJpaRepository.findUserVisiblePostsBeforeModifiedAt(userId, cursorPostedAt,
        cursorPostId, limit);
  }

  @Override
  public void changeStatus(Post post) {
    postJpaRepository.updateStatus(post.getPostId(), post.getStatus());
  }

  @Override
  public Optional<PostStatus> fetchPostStatusByPostId(String postId) {
    return
        postJpaRepository.findPostStatusByPostId(postId);
  }

  @Override
  public Optional<PostEngagementProjection> fetchPostEngagementByPostIdAndUserId(String postId,
      String userId) {
    return postJpaRepository.findPostEngagementByPostIdAndUserId(postId, userId);
  }

  @Override
  public boolean existsById(String postId) {
    return postJpaRepository.existsById(postId);
  }


  @Override
  public void increaseViewCount(String postId) {
    postJpaRepository.increaseViewCount(postId);
  }

  @Override
  public Optional<String> fetchUserIdByPostId(String postId) {
    return
        postJpaRepository.findUserIdByPostId(postId);
  }
}
