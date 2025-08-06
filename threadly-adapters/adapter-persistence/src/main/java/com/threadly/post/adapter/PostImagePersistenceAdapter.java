package com.threadly.post.adapter;

import com.threadly.post.entity.PostEntity;
import com.threadly.post.mapper.PostImageMapper;
import com.threadly.post.PostImage;
import com.threadly.image.ImageStatus;
import com.threadly.post.image.fetch.FetchPostImagePort;
import com.threadly.post.image.fetch.PostImageProjection;
import com.threadly.post.image.save.SavePostImagePort;
import com.threadly.post.image.update.UpdatePostImagePort;
import com.threadly.post.repository.PostImageJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 게시글 이미지 메타 데이터 저장 adapter
 */
@Repository
@RequiredArgsConstructor
public class PostImagePersistenceAdapter implements SavePostImagePort, FetchPostImagePort,
    UpdatePostImagePort {

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

  public List<PostImageProjection> findAllByPostIdAndStatus(String postId, ImageStatus status) {
    return postImageJpaRepository.getPostImageListByPostIdAndStatus(postId, status);
  }


  @Override
  public void updateStatus(String postId, ImageStatus status) {
    postImageJpaRepository.updateStatus(postId, status);
  }

  @Override
  public void updateImageOrder(String imageId, int order) {
    postImageJpaRepository.updateImageOrder(imageId, order);
  }

  @Override
  public void updatePostId(String imageId, String postId) {
    postImageJpaRepository.updatePostId(imageId, postId);
  }

  @Override
  public void finalizeImage(String imageId, String postId, int order) {
    postImageJpaRepository.finalizePostImage(imageId, PostEntity.fromId(postId), order,
        ImageStatus.CONFIRMED);
  }
}
