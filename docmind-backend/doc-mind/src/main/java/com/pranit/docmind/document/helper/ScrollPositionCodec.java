package com.pranit.docmind.document.helper;

import com.pranit.docmind.authentication.exception.InvalidScrollIdException;
import lombok.Builder;
import org.springframework.data.domain.KeysetScrollPosition;
import org.springframework.data.domain.ScrollPosition;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class ScrollPositionCodec {

    private ScrollPositionCodec() {
    }

    // Encode
    public static String encode(final ScrollPosition position, final ScrollDirection direction) {
        if (position == null || position.isInitial()) return null;
        if (!(position instanceof KeysetScrollPosition keysetPosition)) return null;
        final StringBuilder sb = new StringBuilder();
        sb.append("direction=").append(direction.name());
        keysetPosition.getKeys().forEach((key, value) -> sb.append(";").append(key).append("=").append(value));
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    // Decode
    public static DecodedScrollPosition decode(String encodedScrollPosition) {
        if (encodedScrollPosition == null || encodedScrollPosition.isBlank())
            return DecodedScrollPosition.builder()
                    .position(ScrollPosition.keyset())
                    .direction(ScrollDirection.FORWARD)
                    .build();
        try {
            final String decoded = new String(Base64.getUrlDecoder().decode(encodedScrollPosition), StandardCharsets.UTF_8);
            final String[] pairs = decoded.split(";");
            final Map<String, Object> keys = new LinkedHashMap<>();
            ScrollDirection direction = ScrollDirection.FORWARD;
            for (String pair : pairs) {
                final String[] keyValue = pair.split("=", 2);
                if (keyValue.length != 2) continue;
                final String key = keyValue[0];
                final String value = keyValue[1];
                if ("direction".equals(key)) {
                    try {
                        direction = ScrollDirection.valueOf(value.toUpperCase());
                    } catch (IllegalArgumentException ignored) {
                        direction = ScrollDirection.FORWARD;
                    }
                    continue;
                }
                keys.put(key, convertValue(key, value));
            }
            ScrollPosition position;
            if (direction == ScrollDirection.BACKWARD) {
                position = ScrollPosition.backward(keys);
            } else {
                position = ScrollPosition.forward(keys);
            }
            return DecodedScrollPosition.builder()
                    .position(position)
                    .direction(direction)
                    .build();
        } catch (Exception e) {
            throw new InvalidScrollIdException("Invalid scrollId: " + e.getMessage());
        }
    }

    private static Object convertValue(String key, String value) {
        return switch (key) {
            case "documentId" -> UUID.fromString(value);
            case "fileSize", "chunksCreated", "version" -> Long.valueOf(value);
            default -> value;
        };
    }

    public enum ScrollDirection {
        FORWARD,
        BACKWARD
    }

    @Builder
    public record DecodedScrollPosition(
            ScrollPosition position,
            ScrollDirection direction
    ) {
    }
}
