package com.example.potterconsumer.client;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Pure URL encoding for a spell name (e.g. "Wingardium Leviosa"), extracted
 * so it can be unit tested in isolation from any HTTP call.
 */
public final class NameEncoder {

    private NameEncoder() {
    }

    public static String encode(String name) {
        return URLEncoder.encode(name, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
