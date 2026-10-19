package com.example.potterconsumer.api;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** UNIT test for pure query-string parsing. No HttpServer, no socket. */
class QueryParamsTest {

    @Test
    void findsASingleParameter() {
        URI uri = URI.create("/api/spell?name=Wingardium%20Leviosa");

        assertEquals(Optional.of("Wingardium Leviosa"), QueryParams.find(uri, "name"));
    }

    @Test
    void findsAParameterAmongSeveral() {
        URI uri = URI.create("/api/spell?foo=bar&name=Lumos&baz=qux");

        assertEquals(Optional.of("Lumos"), QueryParams.find(uri, "name"));
    }

    @Test
    void decodesPlusAsSpace() {
        URI uri = URI.create("/api/spell?name=Avada+Kedavra");

        assertEquals(Optional.of("Avada Kedavra"), QueryParams.find(uri, "name"));
    }

    @Test
    void returnsEmptyWhenParameterIsAbsent() {
        assertTrue(QueryParams.find(URI.create("/api/spell?foo=bar"), "name").isEmpty());
    }

    @Test
    void returnsEmptyWhenThereIsNoQueryStringAtAll() {
        assertTrue(QueryParams.find(URI.create("/api/spell"), "name").isEmpty());
    }
}
