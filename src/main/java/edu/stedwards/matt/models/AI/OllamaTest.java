package edu.stedwards.matt.models.AI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;


public class OllamaTest {
    public static void main(String[] args) {
        String url = "http://localhost:11434/api/generate";
        
        String jsonPayload = "{"
                + "\"model\": \"llama3.2:3b\","
                + "\"prompt\": \"In one sentence, why is local data privacy important for job seekers?\","
                + "\"stream\": false"
                + "}";

        try {
                    HttpURLConnection connection = (HttpURLConnection) java.net.URI.create(url).toURL().openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    try (java.io.OutputStream output = connection.getOutputStream()) {
                        output.write(jsonPayload.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                    }

            System.out.println("Asking local Ollama model...");
                    StringBuilder response = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), java.nio.charset.StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line).append(System.lineSeparator());
                        }
                    } finally {
                        connection.disconnect();
                    }

                    System.out.println("Response Body:\n" + response);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
