package com.threadly.repository.post;

import com.threadly.entity.post.PostImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageJpaRepository extends JpaRepository<PostImageEntity, String> {


}
