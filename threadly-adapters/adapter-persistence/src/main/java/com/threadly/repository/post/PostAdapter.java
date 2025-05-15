package com.threadly.repository.post;

import com.threadly.ErrorCode;
import com.threadly.entity.post.PostEntity;
import com.threadly.entity.user.UserEntity;
import com.threadly.exception.user.UserException;
import com.threadly.mapper.post.PostMapper;
import com.threadly.post.CreatePostPort;
import com.threadly.post.FetchPostPort;
import com.threadly.post.UpdatePostPort;
import com.threadly.post.response.PostDetailResponse;
import com.threadly.posts.Post;
import com.threadly.repository.user.UserJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 게시글 관련 adapter
 */
@Repository
@RequiredArgsConstructor
public class PostAdapter implements CreatePostPort, FetchPostPort, UpdatePostPort {

  private final PostJpaRepository postJpaRepository;
  private final UserJpaRepository userJpaRepository;


  @Override
  public Post savePost(Post post) {
    UserEntity userEntity = userJpaRepository.findById(post.getUserId())
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

    PostEntity saved = postJpaRepository.save(
        PostEntity.newPost(userEntity, post.getContent())
    );

    return PostMapper.toDomain(saved);
  }

  @Override
  public Optional<Post> findById(String postId) {
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
  public Optional<PostDetailResponse> fetchPostDetailsByPostId(String postId) {
    return
        postJpaRepository.getPostDetailsByPostId(postId);
  }

  @Override
  public List<PostDetailResponse> fetchPostDetailsList() {
    return
        postJpaRepository.getPostDetailsList();
  }
}
