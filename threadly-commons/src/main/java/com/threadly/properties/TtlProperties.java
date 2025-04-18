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
    return Duration.ofMinutes(accessToken);
  }

  public Duration getRefreshToken() {
    return Duration.ofMinutes(refreshToken);
  }

  public Duration getBlacklistToken() {
    return Duration.ofMinutes(blacklistToken);
  }

  public Duration getEmailVerification() {
    return Duration.ofMinutes(emailVerification);
  }

  public Duration getPasswordVerification() {
    return Duration.ofMinutes(passwordVerification);
  }
}
