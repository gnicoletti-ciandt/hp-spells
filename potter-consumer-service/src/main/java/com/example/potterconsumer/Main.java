package com.example.potterconsumer;

import com.example.potterconsumer.api.SpellHttpServer;
import com.example.potterconsumer.client.PotterApiClient;

public class Main {

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "4100"));
        String potterApiBaseUrl = System.getenv()
                .getOrDefault("POTTER_API_BASE_URL", "https://potterapi-fedeperin.vercel.app");

        PotterApiClient client = new PotterApiClient(potterApiBaseUrl);
        SpellHttpServer server = new SpellHttpServer(port, client);
        server.start();

        System.out.println("potter-consumer-service listening on port " + server.port()
                + " (upstream: " + potterApiBaseUrl + ")");
    }
}
