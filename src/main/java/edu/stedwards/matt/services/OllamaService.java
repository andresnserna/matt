package edu.stedwards.matt.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OllamaService {
    private static final String OLLAMA_TAGS_URL = "http://localhost:11434/api/tags";
    private static final String OLLAMA_GENERATE_URL = "http://localhost:11434/api/generate";
    private static final Pattern TAG_NAME_PATTERN = Pattern.compile("\"name\"\\s*:\\s*\"([^\"]+)\"");

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .build();

    /** Run the Ollama model-list operation and return installed model names. */
    public List<String> listInstalledModels() {
        Set<String> models = new LinkedHashSet<>();

        String cliOutput = runOllamaCommand(5, "list");
        if (cliOutput != null && !cliOutput.isBlank()) {
            String[] lines = cliOutput.split("\\r?\\n");
            boolean passedHeader = false;
            for (String line : lines) {
                if (line == null || line.isBlank()) {
                    continue;
                }
                String trimmed = line.trim();
                if (!passedHeader) {
                    if (trimmed.toUpperCase(Locale.ROOT).startsWith("NAME")) {
                        passedHeader = true;
                    }
                    continue;
                }
                String[] cols = trimmed.split("\\s+");
                if (cols.length > 0 && !cols[0].isBlank()) {
                    models.add(cols[0].trim());
                }
            }
        }

        if (models.isEmpty()) {
            models.addAll(fetchModelsFromApi());
        }

        return new ArrayList<>(models);
    }

    /** Run the Ollama model-inspection operation and return its raw detail output. */
    public String getModelDetails(String modelName) {
        if (modelName == null || modelName.isBlank()) {
            return "";
        }
        String output = runOllamaCommand(8, "show", modelName.trim());
        return output != null ? output : "";
    }

    /** Inspect the local Ollama process state and report Running, Idle, or Disconnected. */
    public String getOllamaStatus() {
        String psOutput = runOllamaCommand(5, "ps");
        String parsed = OllamaUtils.parseStatus(psOutput);
        if (parsed != null && !"Disconnected".equals(parsed)) {
            return parsed;
        }
        return isRunning() ? "Idle" : "Disconnected";
    }

    /** Start or select the requested model as the active local runner. */
    public void refreshModelSelection(String modelName) {
        if (modelName == null || modelName.isBlank()) {
            return;
        }
        if (!ensureRunning()) {
            System.err.println("Cannot load model '" + modelName + "': Ollama server is not reachable.");
            return;
        }

        String escapedModel = modelName.trim().replace("\\", "\\\\").replace("\"", "\\\"");
        String payload = "{\"model\":\"" + escapedModel + "\",\"prompt\":\"\",\"stream\":false,\"keep_alive\":\"10m\"}";

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OLLAMA_GENERATE_URL))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                System.err.println("Ollama preload returned HTTP " + response.statusCode() + " for model " + modelName);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Model preload interrupted: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Could not preload Ollama model '" + modelName + "': " + e.getMessage());
        }
    }

    /** Parse the current Ollama process output and return the active runner name. */
    public String getCurrentRunner() {
        String psOutput = runOllamaCommand(5, "ps");
        return OllamaUtils.parseCurrentRunner(psOutput);
    }

    /** Checks if the local Ollama server is currently running and reachable. */
    public boolean isRunning() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OLLAMA_TAGS_URL))
                    .timeout(Duration.ofSeconds(2))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    /** Starts the Ollama server process if it is not already running. */
    public boolean ensureRunning() {
        if (isRunning()) {
            return true;
        }

        try {
            String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
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

            for (int i = 0; i < 5; i++) {
                Thread.sleep(1000);
                if (isRunning()) {
                    System.out.println("Ollama service started automatically.");
                    return true;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("Could not auto-start Ollama: " + e.getMessage());
        }
        return isRunning();
    }

    /** Validates the API key format and verifies that the Ollama service is reachable. */
    public boolean validateApiKey(String apiKey) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return ensureRunning();
        }
        return apiKey.trim().length() >= 5 && ensureRunning();
    }

    private String runOllamaCommand(int timeoutSeconds, String... args) {
        List<String> command = new ArrayList<>();
        command.add("ollama");
        for (String arg : args) {
            command.add(arg);
        }

        Process process = null;
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            process = pb.start();

            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return null;
            }
            return new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            if (process != null) {
                process.destroyForcibly();
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    private List<String> fetchModelsFromApi() {
        List<String> models = new ArrayList<>();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OLLAMA_TAGS_URL))
                    .timeout(Duration.ofSeconds(3))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 && response.body() != null) {
                Matcher matcher = TAG_NAME_PATTERN.matcher(response.body());
                while (matcher.find()) {
                    String name = matcher.group(1).trim();
                    if (!name.isEmpty()) {
                        models.add(name);
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException ignored) {
            // Ollama is offline; return empty list cleanly
        }
        return models;
    }
}