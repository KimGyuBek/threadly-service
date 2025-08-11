package com.threadly.batch.properties;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "properties.retention")
@Getter
@Setter
public class RetentionProperties {

  ImageRetention image;
  Duration postDeleted;
  Duration userDeleted;

  /**
   * Image Retention
   */
  @Getter
  @Setter
  public static class ImageRetention {

    private Duration deleted;
    private Duration temporary;


  }
}
