package com.threadly.utils;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import java.util.UUID;

/**
 * id 생성하는 utils
 */
public class RandomUtils {

  public static String generateUUID() {
    return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
  }

  public static String generateNanoId() {
    return NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
        NanoIdUtils.DEFAULT_ALPHABET, 16);
  }

}
