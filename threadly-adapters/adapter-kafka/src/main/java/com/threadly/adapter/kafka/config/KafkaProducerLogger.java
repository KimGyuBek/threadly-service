package com.threadly.adapter.kafka.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

/**
 * Kafka Produce 과정에서의 로깅
 */
@Component
@Slf4j
public class KafkaProducerLogger {

  /**
   * Kafka 발행 성공 로깅
   *
   * @param topic
   * @param eventId
   * @param receiverId
   * @param result
   */
  public void logPublishSuccess(String topic, String eventId, String receiverId,
      SendResult<String, Object> result) {
    var metadata = result.getRecordMetadata();

    log.info("Kafka 메시지 발행 성공: topic={}, eventId={}, receiverId={}, partition={}, offset={}",
        topic, eventId, receiverId, metadata.partition(), metadata.offset());
  }

  /**
   * Kafka 발행 실패 로깅
   *
   * @param topic
   * @param eventId
   * @param receiverId
   * @param ex
   */
  public void logPublishFailure(String topic, String eventId, String receiverId, Throwable ex) {
    log.error("Kafka 메시지 발행 실패: topic={}, eventId={}, receiverId={}, ex={}",
        topic, eventId, receiverId, ex.getMessage());
  }

  /**
   * Kafka 발행 실패 재시도 로깅
   * @param topic
   * @param eventId
   * @param receiverId
   * @param ex
   */
  public void logRetryableFailure(String topic, String eventId, String receiverId,
      Throwable ex) {
    log.warn("Kafka 메시지 발행 실패(재시도 예정): topic={}, eventId={}, receiverId={}, ex={}", topic, eventId, receiverId, ex.getMessage() );
  }

}
