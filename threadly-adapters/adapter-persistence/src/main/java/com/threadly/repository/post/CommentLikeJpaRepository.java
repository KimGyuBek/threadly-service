package com.threadly.repository.post;

import com.threadly.entity.post.CommentLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * CommentLikeEntity Jpa Repository
 */
public interface CommentLikeJpaRepository extends JpaRepository<CommentLikeEntity, String> {

}
