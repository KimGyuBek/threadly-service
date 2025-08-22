package com.threadly.commons.file;

import lombok.Getter;

/**
 * 이미지 비율
 */
@Getter
public enum AspectRatio {

  RATIO_1_1(1.0),
  RATIO_4_3(4.0 / 3.0),
  RATIO_3_4(3.0 / 4.0),
  RATIO_16_9(16.0 / 9.0),
  RATIO_9_6(9.6 / 6.0);

  private final double ratio;

  AspectRatio(double ratio) {
    this.ratio = ratio;
  }

  /**
   * 비율 검증
   * @param actualRatio
   * @param tolerance
   * @return
   */
  public boolean isValid(double actualRatio, double tolerance) {
    return Math.abs(this.ratio - actualRatio) <= tolerance;
  }
}
