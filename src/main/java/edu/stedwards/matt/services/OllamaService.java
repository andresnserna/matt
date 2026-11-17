package edu.stedwards.matt.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class OllamaService {
    private static final String OLLAMA_TAGS_URL = "http://localhost:11434/api/tags";
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .build();

    /**
     * Checks if the local Ollama server is currently running and reachable.
     */
    public boolean isRunning() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OLLAMA_TAGS_URL))
                    .timeout(Duration.ofSeconds(2))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    /**
     * Starts the Ollama server process if it is not already running.
     */
    public boolean ensureRunning() {
        if (isRunning()) {
            return true;
        }

        try {
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder pb;
            if (os.contains("win")) {
                pb = new ProcessBuilder("cmd.exe", "/c", "ollama serve");
            } else if (os.contains("mac")) {
                pb = new ProcessBuilder("/bin/sh", "-c", "ollama serve");
            } else {
                pb = new ProcessBuilder("ollama", "serve");
            }
            pb.redirectErrorStream(true);
            pb.start();

            // Poll up to 5 times (approx 5 seconds) for Ollama to spin up
            for (int i = 0; i < 5; i++) {
                Thread.sleep(1000);
                if (isRunning()) {
                    System.out.println("Ollama service started automatically.");
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Could not auto-start Ollama: " + e.getMessage());
        }
        return isRunning();
    }

    /**
     * Validates the API key format and verifies that the Ollama service is reachable.
     */
    public boolean validateApiKey(String apiKey) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            // Local Ollama instances don't strictly require a cloud key, 
            // so we still ensure the local server is running.
            return ensureRunning();
        }
        return apiKey.trim().length() >= 5 && ensureRunning();
    }
}