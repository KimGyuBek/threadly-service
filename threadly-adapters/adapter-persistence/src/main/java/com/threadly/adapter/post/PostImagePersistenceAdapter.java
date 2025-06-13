package com.threadly.adapter.post;

import com.threadly.mapper.post.PostImageMapper;
import com.threadly.post.PostImage;
import com.threadly.post.image.SavePostImagePort;
import com.threadly.repository.post.PostImageJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 게시글 이미지 메타 데이터 저장 adapter
 */
@Repository
@RequiredArgsConstructor
public class PostImagePersistenceAdapter implements SavePostImagePort {

  private final PostImageJpaRepository postImageJpaRepository;

  @Override
  public void savePostImage(PostImage postImage) {
    postImageJpaRepository.save(
        PostImageMapper.toEntity(postImage)
    );
  }
}
