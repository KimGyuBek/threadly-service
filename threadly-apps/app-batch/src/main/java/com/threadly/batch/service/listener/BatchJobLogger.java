package com.threadly.batch.service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * 배치 Job 전용 로거 - 파일에만 저장
 */
@Component
@RequiredArgsConstructor
public class BatchJobLogger {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String LOG_FILE_PATH = "logs/batch/batch-jobs.log";
    
    public void logJobStart(Map<String, Object> logData) {
        writeToFile(logData);
    }
    
    public void logJobComplete(Map<String, Object> logData) {
        writeToFile(logData);
    }
    
    private void writeToFile(Map<String, Object> logData) {
        try {
            // logs 디렉토리 생성
            java.io.File logFile = new java.io.File(LOG_FILE_PATH);
            logFile.getParentFile().mkdirs();
            
            // JSON을 파일에 한 줄로 저장 (역슬래시 없는 순수 JSON)
            try (FileWriter writer = new FileWriter(logFile, true)) {
                writer.write(objectMapper.writeValueAsString(logData) + System.lineSeparator());
            }
        } catch (IOException e) {
            // 파일 저장 실패 시 무시 (콘솔 출력도 안함)
        }
    }
}