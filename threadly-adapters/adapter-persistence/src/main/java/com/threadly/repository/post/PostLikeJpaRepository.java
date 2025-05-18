package com.threadly.repository.post;

import com.threadly.entity.post.PostLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * PostLikeEntity Jpa Repository
 */
public interface PostLikeJpaRepository extends JpaRepository<PostLikeEntity, String> {

}
