package com.threadly.file;

import com.threadly.util.RandomUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UploadImage {

  private String originalFileName;
  private String storedFileName;
  private byte[] content;
  private String contentType;
  private long size;


  public UploadImage(String originalFileName, byte[] content, String contentType, long size) {
    this.storedFileName = RandomUtils.generateNanoId();
    this.originalFileName = originalFileName;
    this.content = content;
    this.contentType = contentType;
    this.size = size;
  }
}
