package com.pranit.docmind.aop.config;

import com.pranit.docmind.aop.aspect.LoggingAspect;
import com.pranit.docmind.aop.aspect.TrackingAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class AopConfig {

    @Bean
    public TrackingAspect trackingAspect() {
        return new TrackingAspect();
    }

    @Bean
    public LoggingAspect loggingAspect() {
        return new LoggingAspect();
    }
}
