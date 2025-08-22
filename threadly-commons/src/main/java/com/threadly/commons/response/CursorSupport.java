package com.threadly.commons.response;

import java.time.LocalDateTime;

public interface CursorSupport {

  public LocalDateTime cursorTimeStamp();

  public String cursorId();


}
