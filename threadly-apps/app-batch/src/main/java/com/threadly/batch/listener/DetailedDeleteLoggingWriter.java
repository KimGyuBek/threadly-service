package com.threadly.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

/**
 * Writer에서 직접 삭제 로깅하는 방식
 * (더 상세한 로깅이 필요한 경우 사용)
 */
@Slf4j
public class DetailedDeleteLoggingWriter implements ItemWriter<String> {
    
    private final ItemWriter<String> delegate;
    private final String entityType;
    private final String deleteStatus;
    
    public DetailedDeleteLoggingWriter(ItemWriter<String> delegate, String entityType, String deleteStatus) {
        this.delegate = delegate;
        this.entityType = entityType;
        this.deleteStatus = deleteStatus;
    }
    
    @Override
    public void write(Chunk<? extends String> chunk) throws Exception {
        // 삭제 전 로깅
        chunk.getItems().forEach(id -> 
            log.info("[{}] {} 상태 아이템 삭제 시작: {}", entityType, deleteStatus, id)
        );
        
        // 실제 삭제 실행
        delegate.write(chunk);
        
        // 삭제 후 로깅
        chunk.getItems().forEach(id -> 
            log.info("[{}] {} 상태 아이템 삭제 완료: {}", entityType, deleteStatus, id)
        );
    }
}