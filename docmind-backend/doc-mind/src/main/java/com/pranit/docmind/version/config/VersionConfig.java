package com.pranit.docmind.version.config;

import com.pranit.docmind.version.service.VersionParser;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class VersionConfig implements WebMvcConfigurer {

    @Override
    public void configureApiVersioning(ApiVersionConfigurer configurer) {
        configurer
                .useRequestHeader("X-API-Version")
                .setVersionParser(new VersionParser())
                .addSupportedVersions("v1")
                .setDefaultVersion("v1")
                .detectSupportedVersions(false);
    }
}
