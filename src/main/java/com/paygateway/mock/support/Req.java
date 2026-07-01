package com.paygateway.mock.support;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Null-safe accessors for the loosely-typed request bodies the mock accepts.
 * Controllers receive the raw body as a String (so any – or no – Content-Type is
 * tolerated); {@link #asMap(String)} turns it into a map and these helpers pull
 * typed values back out when present.
 */
public final class Req {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    private Req() {
    }

    /** Parse a raw JSON body into a map; returns {@code null} for empty or invalid input. */
    public static Map<String, Object> asMap(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return MAPPER.readValue(raw, MAP_TYPE);
        } catch (Exception e) {
            return null;
        }
    }

    public static String str(Map<String, Object> body, String key) {
        if (body == null) {
            return null;
        }
        Object v = body.get(key);
        return v == null ? null : String.valueOf(v);
    }

    public static String str(Map<String, Object> body, String key, String fallback) {
        String v = str(body, key);
        return v == null ? fallback : v;
    }

    public static Integer integer(Map<String, Object> body, String key) {
        if (body == null) {
            return null;
        }
        Object v = body.get(key);
        if (v instanceof Number n) {
            return n.intValue();
        }
        if (v instanceof String s && !s.isBlank()) {
            try {
                return Integer.parseInt(s.trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    public static Integer integer(Map<String, Object> body, String key, int fallback) {
        Integer v = integer(body, key);
        return v == null ? fallback : v;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> metadata(Map<String, Object> body) {
        if (body == null) {
            return null;
        }
        Object v = body.get("metadata");
        if (v instanceof Map<?, ?> m && !m.isEmpty()) {
            return (Map<String, String>) v;
        }
        return null;
    }
}
