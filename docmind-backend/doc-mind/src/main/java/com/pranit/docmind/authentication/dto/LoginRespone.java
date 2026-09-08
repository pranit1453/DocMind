package com.pranit.docmind.authentication.dto;

import lombok.Builder;

import java.util.Set;

@Builder
public record LoginRespone(
        String username,
        Set<String> roles
) {
}
