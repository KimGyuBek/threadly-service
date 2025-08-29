package com.threadly.adapter.kafka.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

  @Bean
  public ProducerFactory<String, Object> producerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

    // JSON 직렬화 설정
    configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

    // 파티셔닝 전략 설정 (키 기반 파티셔닝 - 기본값)
    configProps.put(ProducerConfig.PARTITIONER_CLASS_CONFIG,
        "org.apache.kafka.clients.producer.internals.DefaultPartitioner");

    // 프로듀서 성능 및 안정성 설정
    configProps.put(ProducerConfig.ACKS_CONFIG, "all"); // idempotence를 위해 all 필수
    configProps.put(ProducerConfig.RETRIES_CONFIG, 3); // 재시도 횟수
    configProps.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000); // 재시도 간격
    configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); // 멱등성 보장

    // idempotence 활성화 시 권장 설정들
    configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5); // 동시 요청 수
    configProps.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120000); // 전송 타임아웃 (2분)

    // ObjectMapper 설정을 위한 JsonSerializer 커스터마이징
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.findAndRegisterModules();

    DefaultKafkaProducerFactory<String, Object> factory = new DefaultKafkaProducerFactory<>(
        configProps);
    factory.setValueSerializer(new JsonSerializer<>(objectMapper));

    return factory;
  }

  @Bean
  public KafkaTemplate<String, Object> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }
}