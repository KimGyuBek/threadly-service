package com.threadly.properties;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "properties.ttl")
@Setter
@Getter
public class TtlProperties {

  private long accessToken;
  private long refreshToken;
  private long blacklistToken;
  private long emailVerification;
  private long passwordVerification;

  public Duration getAccessToken() {
    return Duration.ofMillis(accessToken);
  }

  public Duration getRefreshToken() {
    return Duration.ofMillis(refreshToken);
  }

  public Duration getBlacklistToken() {
    return Duration.ofMillis(blacklistToken);
  }

  public Duration getEmailVerification() {
    return Duration.ofMillis(emailVerification);
  }

  public Duration getPasswordVerification() {
    return Duration.ofMillis(passwordVerification);
  }
}
