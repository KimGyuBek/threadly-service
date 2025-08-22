package com.threadly.advice;

import com.threadly.commons.exception.ErrorResponse;
import com.threadly.commons.response.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice()
public class ResponseWrapperAdvice implements ResponseBodyAdvice<Object> {

  @Override
  public boolean supports(MethodParameter returnType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    return true;
//    String packageName = returnType.getDeclaringClass().getPackageName();
//    return !packageName.startsWith("org.springframework.boot.actuate");
  }


  @Override
  public Object beforeBodyWrite(Object body, MethodParameter returnType,
      MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
      ServerHttpRequest request, ServerHttpResponse response) {
    String path = request.getURI().getPath();
    if (path.startsWith("/actuator")) {
      return body;
    }

    if (body instanceof ErrorResponse) {
      return ApiResponse.fail(((ErrorResponse) body).getErrorCode());
    }

    return ApiResponse.success(body);
  }
}
