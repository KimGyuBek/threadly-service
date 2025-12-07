package com.threadly.adapter.kafka.exception;

/**
 * Kafka 발행 실패 예외
 *
 * Resilience4j가 감지
 *
 */
public class KafkaPublishException extends RuntimeException{

  public KafkaPublishException(String message, Throwable cause) {
   super(message, cause);
  }

}
