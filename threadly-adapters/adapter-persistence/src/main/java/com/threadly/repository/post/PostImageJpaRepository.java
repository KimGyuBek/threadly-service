package com.threadly.repository.post;

import com.threadly.entity.post.PostEntity;
import com.threadly.entity.post.PostImageEntity;
import com.threadly.image.ImageStatus;
import com.threadly.post.image.fetch.PostImageProjection;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostImageJpaRepository extends JpaRepository<PostImageEntity, String> {

  /**
   * postId로 이미지 목록 조회
   *
   * @param postId
   * @return
   */
  @Query(
      value = """
          select pi.post_image_id as imageId,
                 pi.image_url     as imageUrl,
                 pi.image_order   as imageOrder
          from post_images pi
          where pi.post_id = :postId
          order by image_order;
          """, nativeQuery = true
  )
  List<PostImageProjection> getPostImageListByPostId(@Param("postId") String postId);

  /**
   * postId, status로 이미지 목록 조회
   *
   * @param postId
   * @param status
   * @return
   */
  @Query(
      """
          select pi.postImageId as imageId,
          pi.imageUrl as imageUrl,
          pi.imageOrder as imageOrder
          from PostImageEntity pi
          where pi.post.postId = :postId and pi.status = :status
          order by imageOrder
          """
  )
  List<PostImageProjection> getPostImageListByPostIdAndStatus(@Param("postId") String postId,
      @Param("status") ImageStatus status);

  /**
   * postId에 해당하는 이미지를 soft delete 처리
   *
   * @param postId
   */
  @Modifying
  @Query(value = """
      update post_images
      set deleted_at = :deletedAt
      where post_id = :postId
      """, nativeQuery = true
  )
  void softDeleteByPostId(@Param("postId") String postId,
      @Param("deletedAt") LocalDateTime deletedAt);

  /**
   * status 변경
   *
   * @param postId
   * @param status
   */
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
      update PostImageEntity pi
      set pi.status = :status
      where pi.post.postId = :postId
      """)
  void updateStatus(@Param("postId") String postId, @Param("status") ImageStatus status);


  /**
   * imageOrder 변경
   *
   * @param imageId
   * @param order
   */
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(value = """
      update post_images
      set image_order = :order
      where post_image_id = :imageId;
      """, nativeQuery = true
  )
  void updateImageOrder(@Param("imageId") String imageId, @Param("order") int order);

  /**
   * postId 설정
   *
   * @param imageId
   * @param postId
   */
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(value = """
      update post_images
      set post_id = :postId
      where post_image_id = :imageId;
      """, nativeQuery = true
  )
  void updatePostId(@Param("imageId") String imageId, @Param("postId") String postId);

  /**
   * 게시글 이미지 업로드 확정 처리
   *
   * @param imageId
   * @param postId
   * @param order
   */
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
      update PostImageEntity pi
      set pi.post = :post,
      pi.status = :status,
      pi.imageOrder = :order
      where pi.postImageId = :imageId
      """)
  void finalizePostImage(@Param("imageId") String imageId, @Param("post") PostEntity postEntity,
      @Param("order") int order, @Param("status") ImageStatus status);

  List<PostImageEntity> findAllByPostImageId(String postImageId);

  List<PostImageEntity> findAllByPost_PostId(String postPostId);
}
