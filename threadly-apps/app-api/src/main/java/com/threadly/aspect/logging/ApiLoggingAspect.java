package com.threadly.aspect.logging;

import static com.threadly.util.LogFormatUtils.debugLog;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Api logging aspect
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class ApiLoggingAspect {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Pointcut("execution(* com.threadly..controller..*(..))")
  public void controllerMethod() {
  }

  @Around("controllerMethod()")
  public Object logRequestResponse(ProceedingJoinPoint pjp) throws Throwable {
    String className = pjp.getSignature().getDeclaringTypeName();
    String methodName = pjp.getSignature().getName();
    Object[] args = pjp.getArgs();

    long start = System.currentTimeMillis();

    debugLog(pjp, getRequest(), "Request", toJson(
        new RequestLog(className, methodName, args)));

    try {
      Object result = pjp.proceed();
//      long duration = System.currentTimeMillis() - start;

//      debugLog(pjp, getRequest(), "Response", toJson(
//          new ResponseLog(className, methodName, args, duration)));

      return result;

    } catch (Exception e) {
      long duration = System.currentTimeMillis() - start;
//      debugErrorLog(pjp, getRequest(), toJson(
//          new ErrorLog(className, methodName, args, duration, e)));
      log.debug(e.getMessage(), e);
      throw e;


    }


  }

  private HttpServletRequest getRequest() {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

    if (attributes == null) {
      throw new IllegalStateException("No request attributes found");
    }
    return attributes.getRequest();
  }


  private String toJson(Object obj) {
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (Exception e) {
      return obj.toString();
    }
  }

  /*Request Log*/
  record RequestLog(String className, String methodName, Object[] args) {

  }

  /*Response Log*/
  record ResponseLog(String className, String methodName, Object[] args, long duration) {

  }

  /*Error Log*/
  record ErrorLog(String className, String methodName, Object[] args, long duration,
                  Exception exception) {

  }

}
