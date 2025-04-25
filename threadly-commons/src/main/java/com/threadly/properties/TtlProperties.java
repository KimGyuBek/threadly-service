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
    return Duration.ofSeconds(accessToken);
  }

  public Duration getRefreshToken() {
    return Duration.ofSeconds(refreshToken);
  }

  public Duration getBlacklistToken() {
    return Duration.ofSeconds(blacklistToken);
  }

  public Duration getEmailVerification() {
    return Duration.ofSeconds(emailVerification);
  }

  public Duration getPasswordVerification() {
    return Duration.ofSeconds(passwordVerification);
  }
}
