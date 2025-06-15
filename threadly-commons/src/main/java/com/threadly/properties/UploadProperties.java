package com.threadly.properties;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

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

  /**
   * 최대 허용 크기
   */
  private DataSize maxSize;


  private Map<String, String> allowTypes;

  private List<String> allowExtensions;

  private AspectRatio aspectRatio;

  /**
   * type에 일치하는 extension map
   *
   * @return
   */
  public Map<String, String> getMimeToExtensions() {
    return
        allowTypes.entrySet().stream().collect(
            Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey,
                (existing, replacement) -> existing)
        );
  }


  @Data
  public static class AspectRatio {
    private double target;
    private double tolerance;
  }
}
