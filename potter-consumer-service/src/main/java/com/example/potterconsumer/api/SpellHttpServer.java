package com.example.potterconsumer.api;

import com.example.potterconsumer.client.PotterApiClient;
import com.example.potterconsumer.client.PotterApiException;
import com.example.potterconsumer.client.SpellNotFoundException;
import com.example.potterconsumer.client.SpellResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Optional;

/**
 * potter-consumer-service: a small HTTP API wrapping potterapi and exposing
 * a single, simplified endpoint:
 *
 * <pre>GET /api/spell?name=Lumos</pre>
 * <pre>GET /api/spells</pre>  (all valid spells, sorted A-Z)
 */
public class SpellHttpServer {

    private final HttpServer server;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SpellHttpServer(int port, PotterApiClient potterApiClient) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/spell", withCors(exchange -> handleGetSpell(exchange, potterApiClient)));
        server.createContext("/api/spells", withCors(exchange -> handleListSpells(exchange, potterApiClient)));
        server.createContext("/health", withCors(this::handleHealth));
        server.setExecutor(null);
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    public int port() {
        return server.getAddress().getPort();
    }

    private void handleGetSpell(HttpExchange exchange, PotterApiClient potterApiClient) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, new ErrorDto("method not allowed"));
            return;
        }

        Optional<String> name = QueryParams.find(exchange.getRequestURI(), "name");
        if (name.isEmpty() || name.get().isBlank()) {
            sendJson(exchange, 400, new ErrorDto("missing required query parameter 'name'"));
            return;
        }

        try {
            SpellResponse upstream = potterApiClient.getSpell(name.get().trim());
            sendJson(exchange, 200, SpellDtoMapper.toDto(upstream));
        } catch (SpellNotFoundException e) {
            sendJson(exchange, 404, new ErrorDto("no spell found for name '" + name.get() + "'"));
        } catch (PotterApiException e) {
            sendJson(exchange, 502, new ErrorDto("upstream potterapi error: " + e.getMessage()));
        }
    }

    private void handleListSpells(HttpExchange exchange, PotterApiClient potterApiClient) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, new ErrorDto("method not allowed"));
            return;
        }

        try {
            sendJson(exchange, 200, SpellDtoMapper.toSortedDtos(potterApiClient.getAllSpells()));
        } catch (PotterApiException e) {
            sendJson(exchange, 502, new ErrorDto("upstream potterapi error: " + e.getMessage()));
        }
    }

    private void handleHealth(HttpExchange exchange) throws IOException {
        sendJson(exchange, 200, Map.of("status", "UP"));
    }

    private HttpHandler withCors(HttpHandler delegate) {
        return exchange -> {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            delegate.handle(exchange);
        };
    }

    private void sendJson(HttpExchange exchange, int status, Object body) throws IOException {
        byte[] bytes = objectMapper.writeValueAsBytes(body);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
