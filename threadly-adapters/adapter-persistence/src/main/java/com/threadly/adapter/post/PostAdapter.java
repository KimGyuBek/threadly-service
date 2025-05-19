package com.threadly.adapter.post;

import com.threadly.ErrorCode;
import com.threadly.entity.post.PostEntity;
import com.threadly.entity.user.UserEntity;
import com.threadly.exception.user.UserException;
import com.threadly.mapper.post.PostMapper;
import com.threadly.post.SavePostPort;
import com.threadly.post.FetchPostPort;
import com.threadly.post.UpdatePostPort;
import com.threadly.post.response.PostDetailResponse;
import com.threadly.posts.Post;
import com.threadly.repository.post.PostJpaRepository;
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
public class PostAdapter implements SavePostPort, FetchPostPort, UpdatePostPort {

  private final PostJpaRepository postJpaRepository;
  private final UserJpaRepository userJpaRepository;


  @Override
  public Post savePost(Post post) {
    /*사용자 조회*/
    UserEntity userEntity = userJpaRepository.findById(post.getUserId())
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

    /*게시글 조회*/
    PostEntity saved = postJpaRepository.save(
        PostEntity.newPost(userEntity, post)
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
  public Optional<PostDetailResponse> findPostDetailsByPostId(String postId) {
    return
        postJpaRepository.getPostDetailsByPostId(postId);
  }

  @Override
  public List<PostDetailResponse> findUserVisiblePostList() {
    return
        postJpaRepository.getUserVisiblePostList();
  }

  @Override
  public void changeStatus(Post post) {
    postJpaRepository.updateStatus(post.getPostId(), post.getStatus());
  }
}
