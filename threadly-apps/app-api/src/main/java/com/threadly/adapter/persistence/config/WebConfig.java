package com.threadly.adapter.persistence.config;

import com.threadly.global.interceptor.UserProfileSettingInterceptor;
import com.threadly.commons.properties.UploadProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

  private final UploadProperties uploadProperties;

  private final UserProfileSettingInterceptor userProfileSettingInterceptor;

  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    configurer.defaultContentType(MediaType.APPLICATION_JSON);
    configurer.mediaType("json", MediaType.APPLICATION_JSON);
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/images/posts/**")
        .addResourceLocations(
            "file:" + uploadProperties.getLocation().getPostImage());
    registry.addResourceHandler("/images/profiles/**")
        .addResourceLocations(
            "file:" + uploadProperties.getLocation().getProfileImage()
        );
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(userProfileSettingInterceptor)
        .addPathPatterns("/api/me/profile")
        .order(1);
  }
}
