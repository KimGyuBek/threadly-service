package com.threadly.adapter.post;

import com.threadly.entity.post.PostEntity;
import com.threadly.mapper.post.PostMapper;
import com.threadly.post.fetch.FetchPostPort;
import com.threadly.post.fetch.PostDetailProjection;
import com.threadly.post.save.SavePostPort;
import com.threadly.post.update.UpdatePostPort;
import com.threadly.post.fetch.PostEngagementProjection;
import com.threadly.posts.Post;
import com.threadly.posts.PostStatusType;
import com.threadly.repository.post.PostJpaRepository;
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
public class PostAdapter implements SavePostPort, FetchPostPort, UpdatePostPort {

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
  public Optional<PostDetailProjection> fetechPostDetailsByPostIdAndUserId(String postId,
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
  public Optional<PostStatusType> fetchPostStatusByPostId(String postId) {
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
}
