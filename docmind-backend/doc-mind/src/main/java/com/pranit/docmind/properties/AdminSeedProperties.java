package com.pranit.docmind.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "docmind.default.credential")
public record AdminSeedProperties(
        String username,
        String password
) {
}
