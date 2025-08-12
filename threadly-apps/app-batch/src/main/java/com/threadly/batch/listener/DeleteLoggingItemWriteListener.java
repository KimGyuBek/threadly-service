package com.threadly.batch.listener;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;
import org.springframework.stereotype.Component;

/**
 * 삭제되는 아이템별 로깅을 위한 ItemWriteListener
 */
@Slf4j
@Component
public class DeleteLoggingItemWriteListener implements ItemWriteListener<String> {

  private final String entityType;
  private final String deleteStatus;

  public DeleteLoggingItemWriteListener() {
    this("Unknown", "Unknown");
  }

  public DeleteLoggingItemWriteListener(String entityType, String deleteStatus) {
    this.entityType = entityType;
    this.deleteStatus = deleteStatus;
  }

//    @Override
//    public void beforeWrite(Chunk<? extends String> items) {
//        List<? extends String> itemList = items.getItems();
//        log.info("[{}] {} 상태 아이템 {}개 삭제 시작: {}",
//                entityType, deleteStatus, itemList.size(), itemList);
//    }

  @Override
  public void afterWrite(Chunk<? extends String> items) {
    List<? extends String> itemList = items.getItems();
    itemList.forEach(
        item ->
            log.info("[{}] {} 상태 id: {} 삭제 완료", entityType, deleteStatus, item)
    );
  }

//    @Override
//    public void onWriteError(Exception exception, Chunk<? extends String> items) {
//        List<? extends String> itemList = items.getItems();
//        log.error("[{}] {} 상태 아이템 {}개 삭제 실패: {}, 에러: {}",
//                entityType, deleteStatus, itemList.size(), itemList, exception.getMessage());
//    }
}