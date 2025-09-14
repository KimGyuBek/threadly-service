package com.threadly.core.port.post.in.update.view;


/**
 * 게시글 조회수 증가 usecase
 */
 public interface IncreaseViewCountUseCase {

 /**
  * 게시글 조회 수 증가
  * @param postId
  * @param userId
  */
 void increaseViewCount(String postId, String userId);


}
