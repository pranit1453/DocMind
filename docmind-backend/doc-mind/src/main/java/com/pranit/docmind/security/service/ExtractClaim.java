package com.pranit.docmind.security.service;

import io.jsonwebtoken.Claims;

import java.util.UUID;

public interface ExtractClaim {

    String getUsernameFromAccessToken(Claims claims);

    UUID getUserIdFromRefreshToken(Claims claims);

    UUID getUserIdFromAccessToken(Claims claims);

    String getJtiFromRefreshToken(Claims claims);

    Claims validateAndParseToken(String token);

    boolean isRefreshToken(Claims claims);

    boolean isAccessToken(Claims claims);

    String getSessionIdFromAccessToken(Claims claims);

    String getSessionIdFromRefeshToken(Claims claims);
}
