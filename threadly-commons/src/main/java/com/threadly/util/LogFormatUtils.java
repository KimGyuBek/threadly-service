package com.threadly.util;


import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;

/**
 * 로그 포맷 유틸
 */
@Slf4j
public class LogFormatUtils {

  public static String getCaller() {
    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

    if (stackTrace.length > 3) {
      StackTraceElement caller = stackTrace[3];
      return caller.getClassName() + "." + caller.getMethodName() + "()";
    }

    return "UnknownCaller";
  }

  /*
   * 성공 로그
   */
  public static void logSuccess(String message) {
    log.info("SUCCESS: {} | {}", getCaller(), message);
  }

  public static void logSuccess(JoinPoint joinPoint) {
    Object[] args = joinPoint.getArgs();

    log.info("SUCCESS: {} | args={}", getCaller(), args);
  }

  /*
   * 실패 로그
   */
  public static void logFailure(String message) {
    log.info("FAIL: {} | {}", getCaller(), message);
  }

  public static void logFailure(JoinPoint joinPoint, Exception e) {
    Object[] args = joinPoint.getArgs();

    log.info("FAIL: {} | args={} | Exception: {}", getCaller(), args,
        e.getMessage());
  }

  /*
   * debug log
   */
  public static void debugLog(JoinPoint joinPoint, String message) {
    String methodName = joinPoint.getSignature().getName();
    String className = joinPoint.getSignature().getDeclaringTypeName();
    Object[] args = joinPoint.getArgs();
    log.debug(
        "\n\n========== DEBUG ==========\n" +
            "Location : {}.{}()\n" +
            "args : {}\n" +
            "Message  : {}\n\n",
        className, methodName, args, message
    );
  }

  public static void debugLog(JoinPoint joinPoint, HttpServletRequest request, String title,
      String desc) {
    String methodName = joinPoint.getSignature().getName();
    String className = joinPoint.getSignature().getDeclaringTypeName();
    Object[] args = joinPoint.getArgs();

    log.debug(
        "\n\n========== [{}] ==========\n" +
            "Location : {}.{}()\n" +
            "Args : {}\n" +
            "Headers : {}\n" +
            "Desc : {}\n\n" ,
            title, className, methodName, args, getHeaders(request), desc);
  }

  public static void debugErrorLog(JoinPoint joinPoint, HttpServletRequest request,
      String desc) {
    String methodName = joinPoint.getSignature().getName();
    String className = joinPoint.getSignature().getDeclaringTypeName();
    Object[] args = joinPoint.getArgs();

    log.error(
        "\n\n========== [Error] ==========\n" +
            "Location : {}.{}()\n" +
            "Args : {}\n" +
            "Headers : {}\n" +
            "Desc : {}\n" ,
            className, methodName, args, getHeaders(request), desc
    );
  }

  private static String getHeaders(HttpServletRequest request) {
    StringBuilder headers = new StringBuilder();
    Enumeration<String> headerNames = request.getHeaderNames();

    if (headerNames != null) {
      while (headerNames.hasMoreElements()) {
        String headerName = headerNames.nextElement();
        headers.append(headerName)
            .append("=")
            .append(request.getHeader(headerName))
            .append(", ");
      }

      if (headers.length() > 0) {
        headers.setLength(headers.length() - 2);
      }
    }

    return headers.toString();
  }

}
