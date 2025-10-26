package com.threadly.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info().title("Threadly Service API")
            .version("v1")
            .description("Threadly Service API 명세"))
        .components(
            new Components().addSecuritySchemes("bearerAuth",
                new SecurityScheme().type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")))
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
  }

  @Bean
  public GroupedOpenApi allApi() {
    return GroupedOpenApi.builder()
        .group("all")
        .pathsToMatch("/api/**")
        .build();
  }

  @Bean
  public GroupedOpenApi authApi() {
    return GroupedOpenApi.builder()
        .group("auth")
        .packagesToScan("com.threadly.auth")
        .pathsToMatch("/api/auth/**")
        .build();
  }

  @Bean
  public GroupedOpenApi usersApi() {
    return GroupedOpenApi.builder()
        .group("users")
        .packagesToScan("com.threadly.user")
        .pathsToMatch("/api/users/**", "/api/me/**")
        .build();
  }

  @Bean
  public GroupedOpenApi postsApi() {
    return GroupedOpenApi.builder()
        .group("posts")
        .packagesToScan("com.threadly.post")
        .pathsToMatch("/api/posts/**", "/api/post-images/**")
        .build();
  }

  @Bean
  public GroupedOpenApi followApi() {
    return GroupedOpenApi.builder()
        .group("follow")
        .packagesToScan("com.threadly.follow")
        .pathsToMatch("/api/follows/**")
        .build();
  }

}
