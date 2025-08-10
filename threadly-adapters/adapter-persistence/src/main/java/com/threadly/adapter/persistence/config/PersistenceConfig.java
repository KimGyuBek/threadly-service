package com.threadly.adapter.persistence.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersistenceConfig {

  @PersistenceContext
  public EntityManager entityManager;

}
