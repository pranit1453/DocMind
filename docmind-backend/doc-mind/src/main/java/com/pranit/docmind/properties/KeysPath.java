package com.pranit.docmind.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "keys")
public record KeysPath(
        String privateKeyPath,
        String publicKeyPath
) {
}
