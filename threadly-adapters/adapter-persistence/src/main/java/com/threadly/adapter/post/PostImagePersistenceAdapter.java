package com.threadly.adapter.post;

import com.threadly.mapper.post.PostImageMapper;
import com.threadly.post.PostImage;
import com.threadly.post.image.fetch.FetchPostImagePort;
import com.threadly.post.image.fetch.PostImageProjection;
import com.threadly.post.image.save.SavePostImagePort;
import com.threadly.repository.post.PostImageJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 게시글 이미지 메타 데이터 저장 adapter
 */
@Repository
@RequiredArgsConstructor
public class PostImagePersistenceAdapter implements SavePostImagePort, FetchPostImagePort {

  private final PostImageJpaRepository postImageJpaRepository;

  @Override
  public void savePostImage(PostImage postImage) {
    postImageJpaRepository.save(
        PostImageMapper.toEntity(postImage)
    );
  }

  @Override
  public List<PostImageProjection> fetchPostImageByPostId(String postId) {
    return postImageJpaRepository.getPostImageListByPostId(postId);
  }
}
