package com.pranit.docmind.aop.aspect;

import com.pranit.docmind.ai.dto.QueryResponse;
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
        final String methodName = joinPoint.getSignature().toShortString();
        final long startTime = System.nanoTime();
        log.info("Starting method: {}", methodName);
        try {
            final Object result = joinPoint.proceed();
            final long elapsedNanos = System.nanoTime() - startTime;
            String executionTime = formatExecutionTime(elapsedNanos);
            log.info("Ending method: {} | Time: {}", methodName, executionTime);
            if (result instanceof QueryResponse response)
                return QueryResponse.builder()
                        .content(response.content())
                        .responseTime(executionTime)
                        .build();
            return result;
        } catch (Exception exception) {
            final long elapsedNanos = System.nanoTime() - startTime;
            log.error("Error in method: {} | Time: {} | Message: {}", methodName, formatExecutionTime(elapsedNanos), exception.getMessage(), exception);
            throw exception;
        }
    }

    private String formatExecutionTime(final long elapsedNanos) {
        final long totalMillis = elapsedNanos / 1_000_000;
        final long minutes = totalMillis / 60_000;
        final long seconds = (totalMillis % 60_000) / 1_000;
        final long millis = totalMillis % 1_000;
        return String.format("%02d:%02d:%03d", minutes, seconds, millis);
    }
}
