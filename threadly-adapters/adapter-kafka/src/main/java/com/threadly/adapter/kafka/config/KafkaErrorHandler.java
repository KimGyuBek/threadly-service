package com.threadly.adapter.kafka.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.SuccessCallback;

@Slf4j
@Component
public class KafkaErrorHandler {

  /**
   * 성공 콜백 - 메시지 전송 성공 시 호출
   */
  public SuccessCallback<SendResult<String, Object>> successCallback(String eventId,
      String receiverId) {
    return result -> {
      var metadata = result.getRecordMetadata();
      log.info(
          "Kafka 메시지 전송 성공: eventId={}, receiverId={}, topic={}, partition={}, offset={}, timestamp={}",
          eventId, receiverId,
          metadata.topic(), metadata.partition(), metadata.offset(), metadata.timestamp());
    };
  }

  /**
   * 실패 콜백 - 메시지 전송 실패 시 호출
   */
  public FailureCallback failureCallback(String eventId, String receiverId) {
    return throwable -> {
      log.error("Kafka 메시지 전송 실패: eventId={}, receiverId={}, error={}",
          eventId, receiverId, throwable.getMessage(), throwable);
    };
  }

}