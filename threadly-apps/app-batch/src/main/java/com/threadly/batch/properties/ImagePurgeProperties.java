package com.threadly.batch.properties;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "properties.image")
public record ImagePurgeProperties(
    Duration retention
) {

}
