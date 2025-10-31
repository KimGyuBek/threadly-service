package com.threadly.adapter.kafka.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.SuccessCallback;

/**
 * KafkaErrorHandler 테스트
 */
class KafkaErrorHandlerTest {

  private KafkaErrorHandler kafkaErrorHandler;

  @BeforeEach
  void setUp() {
    kafkaErrorHandler = new KafkaErrorHandler();
  }

  @Nested
  @DisplayName("successCallback 테스트")
  class SuccessCallbackTest {

    /*[Case #1] successCallback이 정상적으로 생성되어야 한다*/
    @DisplayName("successCallback - 콜백이 정상적으로 생성되어야 한다")
    @Test
    public void successCallback_shouldBeCreatedSuccessfully() throws Exception {
      //given
      String eventId = "event-123";
      String receiverId = "user-456";

      //when
      SuccessCallback<SendResult<String, Object>> callback = kafkaErrorHandler.successCallback(
          eventId, receiverId);

      //then
      assertThat(callback).isNotNull();
    }

    /*[Case #2] successCallback 실행 시 예외가 발생하지 않아야 한다*/
    @DisplayName("successCallback - 실행 시 예외가 발생하지 않아야 한다")
    @Test
    public void successCallback_shouldNotThrowException_whenExecuted() throws Exception {
      //given
      String eventId = "event-123";
      String receiverId = "user-456";

      SendResult<String, Object> mockResult = mock(SendResult.class);
      RecordMetadata mockMetadata = mock(RecordMetadata.class);

      when(mockResult.getRecordMetadata()).thenReturn(mockMetadata);
      when(mockMetadata.topic()).thenReturn("test-topic");
      when(mockMetadata.partition()).thenReturn(0);
      when(mockMetadata.offset()).thenReturn(100L);
      when(mockMetadata.timestamp()).thenReturn(System.currentTimeMillis());

      SuccessCallback<SendResult<String, Object>> callback = kafkaErrorHandler.successCallback(
          eventId, receiverId);

      //when & then
      // 예외가 발생하지 않으면 테스트 성공
      callback.onSuccess(mockResult);
    }
  }

  @Nested
  @DisplayName("failureCallback 테스트")
  class FailureCallbackTest {

    /*[Case #1] failureCallback이 정상적으로 생성되어야 한다*/
    @DisplayName("failureCallback - 콜백이 정상적으로 생성되어야 한다")
    @Test
    public void failureCallback_shouldBeCreatedSuccessfully() throws Exception {
      //given
      String eventId = "event-789";
      String receiverId = "user-999";

      //when
      FailureCallback callback = kafkaErrorHandler.failureCallback(eventId, receiverId);

      //then
      assertThat(callback).isNotNull();
    }

    /*[Case #2] failureCallback 실행 시 예외가 발생하지 않아야 한다*/
    @DisplayName("failureCallback - 실행 시 예외가 발생하지 않아야 한다")
    @Test
    public void failureCallback_shouldNotThrowException_whenExecuted() throws Exception {
      //given
      String eventId = "event-789";
      String receiverId = "user-999";
      Throwable testException = new RuntimeException("Test exception");

      FailureCallback callback = kafkaErrorHandler.failureCallback(eventId, receiverId);

      //when & then
      // 예외가 발생하지 않으면 테스트 성공
      callback.onFailure(testException);
    }

    /*[Case #3] 다양한 예외 타입에 대해 failureCallback이 동작해야 한다*/
    @DisplayName("failureCallback - 다양한 예외 타입을 처리할 수 있어야 한다")
    @Test
    public void failureCallback_shouldHandleVariousExceptionTypes() throws Exception {
      //given
      String eventId = "event-111";
      String receiverId = "user-222";

      FailureCallback callback = kafkaErrorHandler.failureCallback(eventId, receiverId);

      //when & then
      // 여러 예외 타입 테스트
      callback.onFailure(new RuntimeException("Runtime error"));
      callback.onFailure(new IllegalArgumentException("Invalid argument"));
      callback.onFailure(new NullPointerException("Null pointer"));
      // 예외가 발생하지 않으면 테스트 성공
    }
  }

  @Nested
  @DisplayName("콜백 동시 사용 테스트")
  class CombinedCallbackTest {

    /*[Case #1] successCallback과 failureCallback을 같은 eventId로 생성할 수 있어야 한다*/
    @DisplayName("successCallback과 failureCallback을 동일 eventId로 생성 가능해야 한다")
    @Test
    public void callbacks_shouldBeCreatedWithSameEventId() throws Exception {
      //given
      String eventId = "event-333";
      String receiverId = "user-444";

      //when
      SuccessCallback<SendResult<String, Object>> successCallback = kafkaErrorHandler.successCallback(
          eventId, receiverId);
      FailureCallback failureCallback = kafkaErrorHandler.failureCallback(eventId, receiverId);

      //then
      assertThat(successCallback).isNotNull();
      assertThat(failureCallback).isNotNull();
    }
  }
}
