package com.example.potterconsumer.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

/** Client for the third-party https://potterapi-fedeperin.vercel.app API (spells endpoint). */
public class PotterApiClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String baseUrl;

    public PotterApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public SpellResponse getSpell(String name) {
        List<SpellResponse> candidates = fetchSpells("/en/spells?search=" + NameEncoder.encode(name), name);
        return SpellSelector.pick(candidates, name).orElseThrow(() -> new SpellNotFoundException(name));
    }

    public List<SpellResponse> getAllSpells() {
        return fetchSpells("/en/spells", "<all>");
    }

    private List<SpellResponse> fetchSpells(String pathAndQuery, String description) {
        URI uri = URI.create(baseUrl + pathAndQuery);

        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new PotterApiException("Failed to call potterapi for spell '" + description + "'", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PotterApiException("Interrupted while calling potterapi for spell '" + description + "'", e);
        }

        if (response.statusCode() != 200) {
            throw new PotterApiException("Unexpected status code " + response.statusCode()
                    + " from potterapi for spell '" + description + "'");
        }

        try {
            return objectMapper.readValue(response.body(), new TypeReference<List<SpellResponse>>() {});
        } catch (IOException e) {
            throw new PotterApiException("Could not parse potterapi response for spell '" + description + "'", e);
        }
    }
}
