package com.pranit.docmind.ai.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void configureAsyncSupport(final AsyncSupportConfigurer configurer) {
        final SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("mvc-virtual-");
        executor.setVirtualThreads(true);
        configurer.setTaskExecutor(executor);
    }
}