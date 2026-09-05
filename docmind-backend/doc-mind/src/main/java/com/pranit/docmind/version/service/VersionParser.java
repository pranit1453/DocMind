package com.pranit.docmind.version.service;

import org.jspecify.annotations.NullMarked;
import org.springframework.web.accept.ApiVersionParser;

@NullMarked
public final class VersionParser implements ApiVersionParser<Integer> {

    @Override
    public Integer parseVersion(String version) {
        if (!version.matches("v\\d+")) throw new IllegalArgumentException("Invalid API version: " + version);
        return Integer.parseInt(version.substring(1));
    }
}
