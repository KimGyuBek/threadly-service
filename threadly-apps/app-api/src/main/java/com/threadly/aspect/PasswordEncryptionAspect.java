package com.threadly.aspect;

import com.threadly.annotation.PasswordEncryption;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PasswordEncryptionAspect {

  private final PasswordEncoder passwordEncoder;

  @Around("execution(* com.threadly.user.controller..*.*(..))")
  public Object passwordEncryptionAspect(ProceedingJoinPoint pjp) throws Throwable {

    Arrays.stream(pjp.getArgs()).forEach(this::fieldEncryption);

    return pjp.proceed();
  }

  private void fieldEncryption(Object obj) {
    if (obj == null) {
      return;
    }

    FieldUtils.getAllFieldsList(obj.getClass()).stream()
        .filter(
            filter ->
                !(Modifier.isFinal(filter.getModifiers()) && !Modifier.isStatic(
                    filter.getModifiers()))
        )
        .forEach(
            field -> {
              try {
                boolean encryptionTarget = field.isAnnotationPresent(PasswordEncryption.class);
                if (!encryptionTarget) {
                  return;
                }

                Object encryptionField = FieldUtils.readField(field, obj, true);
                if (!(encryptionField instanceof String)) {
                  return;
                }

                String encrypted = passwordEncoder.encode((String) encryptionField);
                FieldUtils.writeField(field, obj, encrypted, true);

              } catch (Exception e) {
                throw new RuntimeException(e);
              }
            });
  }


}
