package com.example.potterconsumer.api;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/** Pure query-string parsing, extracted so it can be unit tested without an HttpServer. */
public final class QueryParams {

    private QueryParams() {
    }

    public static Optional<String> find(URI uri, String name) {
        String query = uri.getRawQuery();
        if (query == null || query.isEmpty()) {
            return Optional.empty();
        }
        for (String pair : query.split("&")) {
            String[] parts = pair.split("=", 2);
            if (parts.length == 2 && parts[0].equals(name)) {
                return Optional.of(URLDecoder.decode(parts[1], StandardCharsets.UTF_8));
            }
        }
        return Optional.empty();
    }
}
