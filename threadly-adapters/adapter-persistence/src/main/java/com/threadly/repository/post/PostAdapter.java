package com.threadly.repository.post;

import com.threadly.ErrorCode;
import com.threadly.entity.post.PostEntity;
import com.threadly.entity.user.UserEntity;
import com.threadly.exception.user.UserException;
import com.threadly.mapper.post.PostMapper;
import com.threadly.port.CreatePostPort;
import com.threadly.posts.Post;
import com.threadly.repository.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 게시글 관련 adapter
 */
@Repository
@RequiredArgsConstructor
public class PostAdapter implements CreatePostPort {

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
}
