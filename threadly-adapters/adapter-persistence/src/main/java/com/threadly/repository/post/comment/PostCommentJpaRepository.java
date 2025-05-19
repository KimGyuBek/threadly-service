package com.threadly.repository.post.comment;

import com.threadly.entity.post.PostCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * PostCommentEntity Jpa Repository
 */
public interface PostCommentJpaRepository extends JpaRepository<PostCommentEntity, String> {

}
