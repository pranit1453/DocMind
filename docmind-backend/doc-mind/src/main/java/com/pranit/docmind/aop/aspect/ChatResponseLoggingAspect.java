package com.pranit.docmind.aop.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class ChatResponseLoggingAspect {

    @Around("@annotation(com.pranit.docmind.aop.annotation.LogChatResponse)")
    public Object logChatResponse(final ProceedingJoinPoint joinPoint) throws Throwable {
        final Object result = joinPoint.proceed();
        if (result instanceof ChatResponse chatResponse) {
            logChatResponse(chatResponse);
        }
        return result;
    }

    private void logChatResponse(final ChatResponse chatResponse) {
        final var usage = chatResponse.getMetadata().getUsage();
        log.info("LLM | promptTokens: {}, completionTokens: {}, totalTokens: {}",
                usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());
        final var generation = chatResponse.getResult();
        if (generation != null) {
            log.info("LLM | finishReason: {}", generation.getMetadata().getFinishReason());
        }
    }
}
