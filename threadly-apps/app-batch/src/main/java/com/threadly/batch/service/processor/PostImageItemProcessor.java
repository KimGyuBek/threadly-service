package com.threadly.batch.service.processor;

import com.threadly.adapter.persistence.post.entity.PostImageEntity;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * PostImage Item Reader
 * <p>
 * PostImageEntity -> postImageId
 * </p>
 */
@Component
public class PostImageItemProcessor implements ItemProcessor<PostImageEntity, String> {

  @Override
  public String process(PostImageEntity item) throws Exception {
    return item.getPostImageId();
  }
}
