package com.threadly.testsupport.fixture;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

public class FixtureLoader {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static <T> T load(String path, TypeReference<T> typeReference) {
    try (InputStream is = new FileInputStream("src/test/resources/fixtures" + path)) {
      return objectMapper.readValue(is, typeReference);

    } catch (IOException e) {
      throw new UncheckedIOException("Fixture 로딩 실패 : " + path, e);
    }

  }

}
