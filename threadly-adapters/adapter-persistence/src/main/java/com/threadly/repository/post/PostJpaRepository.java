package com.threadly.repository.post;

import com.threadly.entity.post.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * PostEntity JpaRepository
 */
public interface PostJpaRepository extends JpaRepository<PostEntity, String> {

}
