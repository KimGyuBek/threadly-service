package com.threadly.repository.post;

import com.threadly.entity.post.PostEntity;
import com.threadly.post.response.PostDetailResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * PostEntity JpaRepository
 */
public interface PostJpaRepository extends JpaRepository<PostEntity, String> {

  /**
   * postId로 content 업데이트
   *
   * @param postId
   * @param content
   */
  @Modifying
  @Query(value =
      "update PostEntity p "
          + "set p.content = :content "
          + "where p.postId = :postId"
  )
  void updatePostContentByPostId(@Param("postId") String postId,
      @Param("content") String content);


  @Query(value = """
      select 
      p.postId as postId,
      u.userId as userId,
      up.nickname as userNickname,
      up.profileImageUrl as userProfileImageUrl,
      p.content as content,
      p.viewCount as viewCount,
      p.modifiedAt as postedAt
      from PostEntity p 
      join p.user u
      join u.userProfile up
      where p.postId = :postId
      """
  )
  Optional<PostDetailResponse> getPostDetailsByPostId(@Param("postId") String postId);

  /**
   * 게시글 상세 리스트 조회, sort = createdAt Desc
   * @return
   */
  @Query(value = """
      select 
            p.postId as postId,
            u.userId as userId,
            up.nickname as userNickname,
            up.profileImageUrl as userProfileImageUrl,
            p.content as content,
            p.viewCount as viewCount,
            p.modifiedAt as postedAt
            from PostEntity p
            join p.user u
            join u.userProfile up
            order by p.createdAt desc
      """
  )
  List<PostDetailResponse> getPostDetailsList();


}
