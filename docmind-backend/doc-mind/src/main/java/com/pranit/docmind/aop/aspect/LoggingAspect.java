package com.pranit.docmind.aop.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class LoggingAspect {

    @Around("@annotation(com.pranit.docmind.aop.annotation.LogExecution)")
    public Object logExecution(final ProceedingJoinPoint joinPoint) throws Throwable {
        final String methodName = joinPoint.getSignature().getName();
        log.info("Initiating to execute method: {}", methodName);
        try {
            final Object result = joinPoint.proceed();
            log.info("Completed successfully method: {}", methodName);
            return result;
        } catch (Exception e) {
            log.error("Method execution failed: {}", methodName, e);
            throw e;
        }
    }
}
