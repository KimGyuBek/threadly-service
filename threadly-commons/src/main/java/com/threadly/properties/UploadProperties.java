package com.threadly.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 업로드 관련 properties
 */
@Data
@Component
@ConfigurationProperties(prefix = "properties.file.upload")
public class UploadProperties {

  /**
   * 실제 파일 저장 경로 *
   */
  private String location;

  /**
   * 접근 URL
   */
  private String accessUrl;

  /**
   * 최대 업로드 허용 이미지 수
   */
  private int maxImageCount;

}
