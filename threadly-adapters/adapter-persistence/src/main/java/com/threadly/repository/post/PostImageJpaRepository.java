package com.threadly.repository.post;

import com.threadly.entity.post.PostImageEntity;
import com.threadly.post.image.fetch.PostImageProjection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostImageJpaRepository extends JpaRepository<PostImageEntity, String> {

  /**
   * postId로 이미지 목록 조회
   * @param postId
   * @return
   */
  @Query(
      value = """
          select pi.image_url   as imageUrl,
                 pi.image_order as imageOrder
          from post_images pi
          where pi.post_id = :postId
          order by image_order;
          """, nativeQuery = true
  )
  List<PostImageProjection> getPostImageListByPostId(@Param("postId") String postId);

}
